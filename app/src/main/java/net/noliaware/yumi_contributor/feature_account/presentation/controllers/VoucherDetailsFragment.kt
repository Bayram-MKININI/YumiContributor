package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.merge
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.QR_CODE_FRAGMENT_TAG
import net.noliaware.yumi_contributor.commun.VOUCHER_ID
import net.noliaware.yumi_contributor.commun.VOUCHER_VALIDATED
import net.noliaware.yumi_contributor.commun.presentation.views.DataValueView
import net.noliaware.yumi_contributor.commun.util.*
import net.noliaware.yumi_contributor.feature_account.domain.model.Voucher
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherCodeData
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStatus
import net.noliaware.yumi_contributor.feature_account.presentation.views.VouchersDetailsView
import net.noliaware.yumi_contributor.feature_account.presentation.views.VouchersDetailsView.*

@AndroidEntryPoint
class VoucherDetailsFragment : AppCompatDialogFragment() {

    companion object {
        fun newInstance(voucherId: String, voucherValidated: Boolean = false) =
            VoucherDetailsFragment().withArgs(
                VOUCHER_ID to voucherId,
                VOUCHER_VALIDATED to voucherValidated
            )
    }

    private var vouchersDetailsView: VouchersDetailsView? = null
    private val viewModel by viewModels<VoucherDetailsFragmentViewModel>()
    var onDataRefreshed: (() -> Unit)? = null

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
            vouchersDetailsView = this as VouchersDetailsView
            vouchersDetailsView?.callback = vouchersDetailsViewCallback
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFlows()
    }

    private fun collectFlows() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            merge(
                viewModel.getVoucherEventsHelper.eventFlow,
                viewModel.getVoucherStatusEventsHelper.eventFlow
            ).collectLatest { sharedEvent ->
                handleSharedEvent(sharedEvent)
                redirectToLoginScreenFromSharedEvent(sharedEvent)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getVoucherEventsHelper.stateFlow.collect { vmState ->
                when (vmState) {
                    is ViewModelState.LoadingState -> Unit
                    is ViewModelState.DataState -> vmState.data?.let { voucher ->
                        bindViewToData(voucher)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getVoucherStatusEventsHelper.stateFlow.collect { vmState ->
                when (vmState) {
                    is ViewModelState.LoadingState -> Unit
                    is ViewModelState.DataState -> vmState.data?.let { voucherStatus ->
                        handleVoucherStatusUpdate(voucherStatus)
                    }
                }
            }
        }
    }

    private fun bindViewToData(voucher: Voucher) {

        vouchersDetailsView?.fillViewWithData(
            VouchersDetailsViewAdapter(
                title = voucher.productLabel.orEmpty(),
                partnerDescription = voucher.partnerInfoText,
                partnerURL = voucher.partnerInfoURL,
                hideDisplayVoucher = viewModel.voucherValidated == true,
            )
        )

        DataValueView.DataValueViewAdapter(
            title = getString(R.string.creation_date),
            value = parseToLongDate(voucher.voucherDate)
        ).also {
            vouchersDetailsView?.addDataValue(it)
        }

        DataValueView.DataValueViewAdapter(
            title = getString(R.string.expiry_date_title),
            value = parseToLongDate(voucher.voucherExpiryDate)
        ).also {
            vouchersDetailsView?.addDataValue(it)
        }

        if (!voucher.productDescription.isNullOrEmpty()) {
            DataValueView.DataValueViewAdapter(
                title = getString(R.string.description),
                value = voucher.productDescription
            ).also {
                vouchersDetailsView?.addDataValue(it)
            }
        }

        if (!voucher.productWebpage.isNullOrEmpty()) {
            DataValueView.DataValueViewAdapter(
                title = getString(R.string.web_page),
                value = voucher.productWebpage
            ).also {
                vouchersDetailsView?.addDataValue(it)
            }
        }

        DataValueView.DataValueViewAdapter(
            title = getString(R.string.retailer_title),
            value = voucher.retailerLabel.orEmpty()
        ).also {
            vouchersDetailsView?.addDataValue(it)
        }

        val retailerAddress = StringBuilder().apply {
            append(voucher.retailerAddress)
            append(getString(R.string.new_line))
            if (voucher.retailerAddressComplement?.isNotEmpty() == true) {
                append(voucher.retailerAddressComplement)
                append(getString(R.string.new_line))
            }
            append(voucher.retailerCity)
            append(getString(R.string.new_line))
            append(voucher.retailerPostcode)
            append(getString(R.string.new_line))
            append(voucher.retailerCountry)
        }.toString()

        DataValueView.DataValueViewAdapter(
            title = getString(R.string.address_title),
            value = retailerAddress
        ).also {
            vouchersDetailsView?.addDataValue(it)
        }

        DataValueView.DataValueViewAdapter(
            title = getString(R.string.mobile),
            value = voucher.retailerCellPhoneNumber.orEmpty()
        ).also {
            vouchersDetailsView?.addDataValue(it)
        }

        DataValueView.DataValueViewAdapter(
            title = getString(R.string.landline),
            value = voucher.retailerPhoneNumber.orEmpty()
        ).also {
            vouchersDetailsView?.addDataValue(it)
        }

        DataValueView.DataValueViewAdapter(
            title = getString(R.string.email),
            value = voucher.retailerEmail.orEmpty()
        ).also {
            vouchersDetailsView?.addDataValue(it)
        }

        if (!voucher.retailerWebsite.isNullOrEmpty()) {
            DataValueView.DataValueViewAdapter(
                title = getString(R.string.web_page),
                value = voucher.retailerWebsite
            ).also {
                vouchersDetailsView?.addDataValue(it)
            }
        }

        vouchersDetailsView?.addLocationView { vouchersDetailsViewCallback.onLocationClicked() }
    }

    private fun handleVoucherStatusUpdate(voucherStatus: VoucherStatus) {
        when (voucherStatus) {
            VoucherStatus.INEXISTENT -> vouchersDetailsView?.setVoucherStatus(getString(R.string.voucher_inexistent))
            VoucherStatus.CANCELED -> vouchersDetailsView?.setVoucherStatus(getString(R.string.voucher_canceled))
            VoucherStatus.USABLE -> Unit
            VoucherStatus.CONSUMED -> vouchersDetailsView?.setVoucherStatus(getString(R.string.voucher_consumed))
        }
    }

    private val vouchersDetailsViewCallback: VouchersDetailsViewCallback by lazy {
        object : VouchersDetailsViewCallback {
            override fun onBackButtonClicked() {
                dismissAllowingStateLoss()
            }

            override fun onPartnerInfoClicked() {
                viewModel.getVoucherEventsHelper.stateData?.let { voucher ->
                    voucher.partnerInfoURL?.let { url ->
                        context?.openWebPage(url)
                    }
                }
            }

            override fun onLocationClicked() {
                viewModel.getVoucherEventsHelper.stateData?.let { voucher ->
                    val latitude = voucher.retailerAddressLatitude
                    val longitude = voucher.retailerAddressLongitude
                    val label = voucher.retailerLabel
                    openMap(context, latitude, longitude, label)
                }
            }

            override fun onDisplayVoucherButtonClicked() {
                viewModel.getVoucherEventsHelper.stateData?.let { voucher ->
                    QrCodeFragment.newInstance(
                        VoucherCodeData(
                            voucherId = voucher.voucherId,
                            productLabel = voucher.productLabel,
                            voucherDate = voucher.voucherDate,
                            voucherExpiryDate = voucher.voucherExpiryDate,
                            voucherCode = voucher.voucherCode,
                            voucherCodeSize = resources.displayMetrics.widthPixels
                        )
                    ).apply {
                        handleDialogClosed = {
                            viewModel.getVoucherEventsHelper.stateData?.voucherId?.let {
                                viewModel.callGetVoucherStatusById(it)
                            }
                        }
                    }.show(
                        childFragmentManager.beginTransaction(),
                        QR_CODE_FRAGMENT_TAG
                    )
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.getVoucherStatusEventsHelper.stateData?.let { voucherStatus ->
            if (voucherStatus == VoucherStatus.CONSUMED) {
                onDataRefreshed?.invoke()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vouchersDetailsView = null
    }
}