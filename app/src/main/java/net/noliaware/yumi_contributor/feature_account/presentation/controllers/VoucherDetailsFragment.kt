package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.content.DialogInterface
import android.os.Bundle
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
import dagger.hilt.android.AndroidEntryPoint
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.DateTime.HOURS_TIME_FORMAT
import net.noliaware.yumi_contributor.commun.DateTime.SHORT_DATE_FORMAT
import net.noliaware.yumi_contributor.commun.FragmentKeys.QR_CODE_REQUEST_KEY
import net.noliaware.yumi_contributor.commun.FragmentKeys.VOUCHER_DETAILS_REQUEST_KEY
import net.noliaware.yumi_contributor.commun.FragmentKeys.VOUCHER_ID_RESULT_KEY
import net.noliaware.yumi_contributor.commun.util.ViewState.DataState
import net.noliaware.yumi_contributor.commun.util.ViewState.LoadingState
import net.noliaware.yumi_contributor.commun.util.collectLifecycleAware
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
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherRetrievalMode
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherRetrievalMode.BENEFICIARY
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherRetrievalMode.BOTH
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStateData
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStatus
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStatus.CANCELLED
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStatus.CONSUMED
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStatus.INEXISTENT
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStatus.USABLE
import net.noliaware.yumi_contributor.feature_account.presentation.views.VouchersDetailsContainerView
import net.noliaware.yumi_contributor.feature_account.presentation.views.VouchersDetailsContainerView.VouchersDetailsViewAdapter
import net.noliaware.yumi_contributor.feature_account.presentation.views.VouchersDetailsContainerView.VouchersDetailsViewCallback

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
    ): View? {
        return inflater.inflate(R.layout.vouchers_details_layout, container, false).apply {
            vouchersDetailsContainerView = this as VouchersDetailsContainerView
            vouchersDetailsContainerView?.callback = vouchersDetailsViewCallback
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFragmentListener()
        collectFlows()
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
                startDate = getString(
                    R.string.created_in_hyphen,
                    voucher.voucherDate?.parseDateToFormat(SHORT_DATE_FORMAT)
                ),
                endDate = mapVoucherEndDate(voucher),
                partnerAvailable = voucher.partnerInfoText?.isNotEmpty() == true,
                partnerLabel = voucher.partnerInfoText,
                voucherDescription = voucher.productDescription,
                moreActionAvailable = voucher.productWebpage?.isNotEmpty() == true,
                retailerLabel = voucher.retailerLabel.orEmpty(),
                retailerAddress = retailerAddress,
                retrievalMode = mapRetrievalMode(voucher.voucherRetrievalMode),
                retrievalModeTextColorRes = mapRetrievalModeTextColor(voucher),
                openVoucherActionNotAvailable = mapOpenVoucherActionNotAvailable(voucher),
                voucherStatusAvailable = voucher.voucherStatus != USABLE,
                voucherStatus = mapVoucherStatus(voucher.voucherStatus)
            )
        )
    }

    private fun mapVoucherEndDate(
        voucher: Voucher
    ) = when (voucher.voucherStatus) {
        USABLE -> getString(
            R.string.expiry_date_value,
            voucher.voucherExpiryDate?.parseDateToFormat(SHORT_DATE_FORMAT)
        )
        CONSUMED -> getString(
            R.string.usage_date_value,
            voucher.voucherUseDate?.parseDateToFormat(SHORT_DATE_FORMAT),
            voucher.voucherUseTime?.parseTimeToFormat(HOURS_TIME_FORMAT)
        )
        CANCELLED -> getString(
            R.string.cancellation_date_value,
            voucher.voucherUseDate?.parseDateToFormat(SHORT_DATE_FORMAT),
            voucher.voucherUseTime?.parseTimeToFormat(HOURS_TIME_FORMAT)
        )
        else -> ""
    }

    private fun mapRetrievalMode(
        retrievalMode: VoucherRetrievalMode?
    ) = when (retrievalMode) {
        BENEFICIARY -> getString(R.string.retrievable_by_beneficiary_only)
        BOTH -> getString(R.string.retrievable_by_beneficiary_also)
        else -> null
    }

    private fun mapRetrievalModeTextColor(
        voucher: Voucher
    ) = when {
        voucher.voucherStatus != USABLE -> R.color.grey_2
        voucher.voucherRetrievalMode == BENEFICIARY -> R.color.color_bittersweet
        else -> R.color.grey_2
    }

    private fun mapOpenVoucherActionNotAvailable(
        voucher: Voucher
    ) = voucher.voucherStatus != USABLE || voucher.voucherRetrievalMode == BENEFICIARY

    private fun mapVoucherStatus(
        voucherStatus: VoucherStatus?
    ) = when (voucherStatus) {
        CONSUMED -> getString(R.string.voucher_consumed)
        CANCELLED -> getString(R.string.voucher_canceled)
        INEXISTENT -> getString(R.string.voucher_inexistent)
        else -> ""
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

    private val vouchersDetailsViewCallback: VouchersDetailsViewCallback by lazy {
        object : VouchersDetailsViewCallback {
            override fun onBackButtonClicked() {
                navDismiss()
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
                                voucherDate = voucher.voucherDate,
                                voucherExpiryDate = voucher.voucherExpiryDate,
                                voucherCode = voucher.voucherCode,
                                voucherCodeSize = resources.displayMetrics.widthPixels
                            )
                        )
                    )
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.getVoucherStateDataEventsHelper.stateData?.let { voucherStateData ->
            if (voucherStateData.voucherStatus == CONSUMED) {
                setFragmentResult(
                    VOUCHER_DETAILS_REQUEST_KEY,
                    bundleOf()
                )
            }
        }
    }

    override fun onDestroyView() {
        vouchersDetailsContainerView = null
        super.onDestroyView()
    }
}