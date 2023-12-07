package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.content.DialogInterface
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.DateTime.HOURS_TIME_FORMAT
import net.noliaware.yumi_contributor.commun.DateTime.SHORT_DATE_FORMAT
import net.noliaware.yumi_contributor.commun.FragmentKeys.QR_CODE_REQUEST_KEY
import net.noliaware.yumi_contributor.commun.FragmentKeys.VOUCHER_DETAILS_REQUEST_KEY
import net.noliaware.yumi_contributor.commun.FragmentKeys.VOUCHER_ID_RESULT_KEY
import net.noliaware.yumi_contributor.commun.util.DecoratedText
import net.noliaware.yumi_contributor.commun.util.ViewState.DataState
import net.noliaware.yumi_contributor.commun.util.ViewState.LoadingState
import net.noliaware.yumi_contributor.commun.util.collectLifecycleAware
import net.noliaware.yumi_contributor.commun.util.decorateWords
import net.noliaware.yumi_contributor.commun.util.getFontFromResources
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.makeCall
import net.noliaware.yumi_contributor.commun.util.navDismiss
import net.noliaware.yumi_contributor.commun.util.openMap
import net.noliaware.yumi_contributor.commun.util.openWebPage
import net.noliaware.yumi_contributor.commun.util.parseDateToFormat
import net.noliaware.yumi_contributor.commun.util.parseTimeToFormat
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.commun.util.safeNavigate
import net.noliaware.yumi_contributor.feature_account.domain.model.Voucher
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherCodeData
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherDeliveryStatus
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherRetrievalMode
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherRetrievalMode.BENEFICIARY
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherRetrievalMode.BOTH
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStateData
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStatus.CANCELLED
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStatus.INEXISTENT
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStatus.USABLE
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStatus.USED
import net.noliaware.yumi_contributor.feature_account.presentation.adapters.VoucherRequestsAdapter
import net.noliaware.yumi_contributor.feature_account.presentation.views.VoucherRequestView
import net.noliaware.yumi_contributor.feature_account.presentation.views.VoucherRequestView.VoucherRequestViewAdapter
import net.noliaware.yumi_contributor.feature_account.presentation.views.VouchersDetailsContainerView
import net.noliaware.yumi_contributor.feature_account.presentation.views.VouchersDetailsContainerView.VouchersDetailsViewAdapter
import net.noliaware.yumi_contributor.feature_account.presentation.views.VouchersDetailsContainerView.VouchersDetailsViewCallback
import net.noliaware.yumi_contributor.feature_login.domain.model.VoucherRequestType

@AndroidEntryPoint
class VoucherDetailsFragment : AppCompatDialogFragment() {

    private var vouchersDetailsContainerView: VouchersDetailsContainerView? = null
    private val args by navArgs<VoucherDetailsFragmentArgs>()
    private val viewModel by viewModels<VoucherDetailsFragmentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.vouchers_details_layout,
        container,
        false
    ).apply {
        vouchersDetailsContainerView = this as VouchersDetailsContainerView
        vouchersDetailsContainerView?.callback = vouchersDetailsViewCallback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFragmentListener()
        collectFlows()
        setUpRequestsDropdownView()
        vouchersDetailsContainerView?.activateLoading(true)
        vouchersDetailsContainerView?.setUpViewLook(
            color = args.categoryUI.categoryColor,
            iconName = args.categoryUI.categoryIcon
        )
    }

    private fun setUpFragmentListener() {
        setFragmentResultListener(
            QR_CODE_REQUEST_KEY
        ) { _, bundle ->
            bundle.getString(VOUCHER_ID_RESULT_KEY)?.let { voucherId ->
                viewModel.callGetVoucherStatusById(voucherId)
            }
        }
    }

    private fun collectFlows() {
        viewModel.getVoucherEventsHelper.eventFlow.collectLifecycleAware(viewLifecycleOwner) { sharedEvent ->
            vouchersDetailsContainerView?.activateLoading(false)
            handleSharedEvent(sharedEvent)
            redirectToLoginScreenFromSharedEvent(sharedEvent)
        }
        viewModel.getVoucherEventsHelper.stateFlow.collectLifecycleAware(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is LoadingState -> Unit
                is DataState -> viewState.data?.let { voucher ->
                    vouchersDetailsContainerView?.activateLoading(false)
                    bindViewToData(voucher)
                }
            }
        }

        viewModel.requestSentEventsHelper.eventFlow.collectLifecycleAware(viewLifecycleOwner) { sharedEvent ->
            handleSharedEvent(sharedEvent)
            redirectToLoginScreenFromSharedEvent(sharedEvent)
        }
        viewModel.requestSentEventsHelper.stateFlow.collectLifecycleAware(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is LoadingState -> Unit
                is DataState -> viewState.data?.let { requestSent ->
                    if (requestSent) {
                        vouchersDetailsContainerView?.displayOngoingRequestsButton()
                    }
                }
            }
        }

        viewModel.getVoucherStateDataEventsHelper.eventFlow.collectLifecycleAware(viewLifecycleOwner) { sharedEvent ->
            redirectToLoginScreenFromSharedEvent(sharedEvent)
        }
        viewModel.getVoucherStateDataEventsHelper.stateFlow.collectLifecycleAware(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is LoadingState -> Unit
                is DataState -> viewState.data?.let { voucherStateData ->
                    handleVoucherStateDataUpdated(voucherStateData)
                }
            }
        }
    }

    private fun bindViewToData(voucher: Voucher) {

        val retailerAddress = StringBuilder().apply {
            append(voucher.retailerAddress)
            append(getString(R.string.new_line))
            if (voucher.retailerAddressComplement?.isNotEmpty() == true) {
                append(voucher.retailerAddressComplement)
                append(getString(R.string.new_line))
            }
            append(voucher.retailerPostcode)
            append(" ")
            append(voucher.retailerCity)
        }.toString()

        vouchersDetailsContainerView?.fillViewWithData(
            VouchersDetailsViewAdapter(
                title = voucher.productLabel.orEmpty(),
                titleCrossed = voucher.voucherStatus != USABLE,
                requestsAvailable = args.requestTypes?.isNotEmpty() == true,
                voucherNumber = mapVoucherNumber(voucher.voucherNumber),
                date = mapVoucherDate(voucher),
                ongoingRequestsAvailable = voucher.voucherOngoingRequestCount > 0,
                partnerAvailable = voucher.partnerInfoText?.isNotEmpty() == true,
                partnerLabel = voucher.partnerInfoText,
                voucherDescription = voucher.productDescription,
                moreActionAvailable = voucher.productWebpage?.isNotEmpty() == true,
                retailerLabel = voucher.retailerLabel.orEmpty(),
                retailerAddress = retailerAddress,
                retrievalMode = mapRetrievalMode(voucher.voucherRetrievalMode),
                voucherStatusAvailable = voucher.voucherDeliveryStatus != VoucherDeliveryStatus.AVAILABLE,
                voucherStatus = mapVoucherStatus(voucher)
            )
        )
    }

    private fun mapVoucherNumber(
        voucherNumber: String
    ) = decorateTextWithFont(
        getString(R.string.voucher_number, voucherNumber),
        listOf(voucherNumber)
    )

    private fun mapVoucherDate(
        voucher: Voucher
    ) = when (voucher.voucherStatus) {
        USABLE -> {
            if (voucher.voucherExpiryDate != null) {
                val expiryDate = voucher.voucherExpiryDate.parseDateToFormat(SHORT_DATE_FORMAT)
                decorateTextWithFont(
                    getString(R.string.usable_end_date, expiryDate),
                    listOf(expiryDate)
                )
            } else {
                val startDate =
                    voucher.voucherStartDate?.parseDateToFormat(SHORT_DATE_FORMAT).orEmpty()
                decorateTextWithFont(
                    getString(R.string.usable_start_date, startDate),
                    listOf(startDate)
                )
            }
        }

        USED -> {
            val usageDate = voucher.voucherUseDate?.parseDateToFormat(SHORT_DATE_FORMAT).orEmpty()
            val usageTime = voucher.voucherUseTime?.parseTimeToFormat(HOURS_TIME_FORMAT).orEmpty()
            decorateTextWithFont(
                getString(
                    R.string.usage_date,
                    getString(R.string.date_time, usageDate, usageTime)
                ),
                listOf(usageDate, usageTime)
            )
        }

        CANCELLED -> {
            val cancellationDate =
                voucher.voucherUseDate?.parseDateToFormat(SHORT_DATE_FORMAT).orEmpty()
            val cancellationTime =
                voucher.voucherUseTime?.parseTimeToFormat(HOURS_TIME_FORMAT).orEmpty()
            decorateTextWithFont(
                getString(
                    R.string.cancellation_date,
                    getString(R.string.date_time, cancellationDate, cancellationTime)
                ),
                listOf(cancellationDate, cancellationTime)
            )
        }

        else -> SpannableString("")
    }

    private fun decorateTextWithFont(
        originalText: String,
        wordsToStyle: List<String>
    ) = originalText.decorateWords(
        wordsToDecorate = wordsToStyle.map {
            DecoratedText(
                textToDecorate = it,
                typeface = context?.getFontFromResources(R.font.omnes_semibold_regular)
            )
        }
    )

    private fun mapRetrievalMode(
        retrievalMode: VoucherRetrievalMode?
    ) = when (retrievalMode) {
        BENEFICIARY -> getString(R.string.retrievable_by_beneficiary_only)
        BOTH -> getString(R.string.retrievable_by_beneficiary_also)
        else -> null
    }

    private fun mapVoucherStatus(
        voucher: Voucher
    ) = when (voucher.voucherStatus) {
        USABLE -> {
            when (voucher.voucherDeliveryStatus) {
                VoucherDeliveryStatus.NON_AVAILABLE -> getString(R.string.voucher_non_available)
                VoucherDeliveryStatus.NON_RETRIEVABLE -> getString(R.string.voucher_non_retrievable)
                else -> ""
            }
        }

        USED -> getString(R.string.voucher_used)
        CANCELLED -> getString(R.string.voucher_canceled)
        INEXISTENT -> getString(R.string.voucher_inexistent)
        null -> ""
    }

    private fun handleVoucherStateDataUpdated(
        voucherStateData: VoucherStateData
    ) {
        val updatedVoucher = viewModel.getVoucherEventsHelper.stateData?.copy(
            voucherStatus = voucherStateData.voucherStatus,
            voucherUseDate = voucherStateData.voucherUseDate,
            voucherUseTime = voucherStateData.voucherUseTime
        )
        updatedVoucher?.let { bindViewToData(it) }
    }

    private fun setUpRequestsDropdownView() {
        vouchersDetailsContainerView?.getRequestSpinner?.adapter = VoucherRequestsAdapter(
            requireContext(),
            (args.requestTypes?.map { voucherRequestType ->
                voucherRequestType.requestTypeLabel
            } ?: emptyList()) + ""
        )
    }

    private val vouchersDetailsViewCallback: VouchersDetailsViewCallback by lazy {
        object : VouchersDetailsViewCallback {
            override fun onBackButtonClicked() {
                navDismiss()
            }

            override fun onRequestSelectedAtIndex(index: Int) {
                args.requestTypes?.get(index)?.let {
                    displayDialogForRequestType(it)
                }
            }

            override fun onOngoingRequestsClicked() {
                findNavController().safeNavigate(
                    VoucherDetailsFragmentDirections.actionVoucherDetailsFragmentToVoucherOngoingRequestListFragment(
                        categoryUI = args.categoryUI,
                        voucherId = args.voucherId
                    )
                )
            }

            override fun onPartnerInfoClicked() {
                viewModel.getVoucherEventsHelper.stateData?.let { voucher ->
                    voucher.partnerInfoURL?.let { url ->
                        context?.openWebPage(url)
                    }
                }
            }

            override fun onMoreButtonClicked() {
                viewModel.getVoucherEventsHelper.stateData?.let { voucher ->
                    voucher.productWebpage?.let { url ->
                        context?.openWebPage(url)
                    }
                }
            }

            override fun onPhoneButtonClicked() {
                viewModel.getVoucherEventsHelper.stateData?.let { voucher ->
                    voucher.retailerCellPhoneNumber?.let { phone ->
                        context?.makeCall(phone)
                    }
                }
            }

            override fun onLocationClicked() {
                viewModel.getVoucherEventsHelper.stateData?.let { voucher ->
                    context.openMap(
                        voucher.retailerAddressLatitude,
                        voucher.retailerAddressLongitude,
                        voucher.retailerLabel
                    )
                }
            }

            override fun onDisplayVoucherButtonClicked() {
                viewModel.getVoucherEventsHelper.stateData?.let { voucher ->
                    findNavController().safeNavigate(
                        VoucherDetailsFragmentDirections.actionVoucherDetailsFragmentToQrCodeFragment(
                            args.categoryUI,
                            VoucherCodeData(
                                voucherId = voucher.voucherId,
                                productLabel = voucher.productLabel,
                                voucherDate = voucher.voucherExpiryDate.orEmpty(),
                                voucherCode = voucher.voucherCode,
                                voucherCodeSize = resources.displayMetrics.widthPixels
                            )
                        )
                    )
                }
            }
        }
    }

    private fun displayDialogForRequestType(selectedRequestType: VoucherRequestType) {
        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.MaterialAlertDialog_rounded
        ).apply {
            val voucherRequestView = layoutInflater.inflate(
                R.layout.voucher_request_layout,
                null
            ) as VoucherRequestView
            voucherRequestView.fillViewWithData(
                VoucherRequestViewAdapter(
                    title = selectedRequestType.requestTypeLabel
                )
            )
            setView(voucherRequestView)
            setPositiveButton(R.string.send) { dialog, _ ->
                viewModel.callSendVoucherRequestWithId(
                    voucherId = args.voucherId,
                    voucherRequestTypeId = selectedRequestType.requestTypeId,
                    voucherRequestComment = voucherRequestView.getUserComment()
                )
                dialog.dismiss()
            }
            setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }.create().show()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (viewModel.getVoucherStateDataEventsHelper.stateData?.voucherStatus == USED || viewModel.requestSentEventsHelper.stateData == true) {
            setFragmentResult(
                VOUCHER_DETAILS_REQUEST_KEY,
                bundleOf()
            )
        }
    }

    override fun onDestroyView() {
        vouchersDetailsContainerView = null
        super.onDestroyView()
    }
}