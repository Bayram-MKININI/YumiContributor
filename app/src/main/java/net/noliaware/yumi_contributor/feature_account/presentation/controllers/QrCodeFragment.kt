package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.CATEGORY_UI
import net.noliaware.yumi_contributor.commun.VOUCHER_CODE_DATA
import net.noliaware.yumi_contributor.commun.util.*
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherCodeData
import net.noliaware.yumi_contributor.feature_account.presentation.views.QrCodeView
import net.noliaware.yumi_contributor.feature_account.presentation.views.QrCodeView.*

@AndroidEntryPoint
class QrCodeFragment : AppCompatDialogFragment() {

    companion object {
        fun newInstance(
            categoryUI: CategoryUI?,
            voucherCodeData: VoucherCodeData
        ) = QrCodeFragment().withArgs(
            CATEGORY_UI to categoryUI,
            VOUCHER_CODE_DATA to voucherCodeData
        )
    }

    private var qrCodeView: QrCodeView? = null
    private val viewModel by viewModels<QrCodeFragmentViewModel>()
    var handleDialogClosed: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.qr_code_layout, container, false).apply {
            qrCodeView = this as QrCodeView
            qrCodeView?.callback = qrCodeViewCallback
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFlows()
        bindViewToData()
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.stateFlow.flowWithLifecycle(lifecycle).collect { vmState ->
                when (vmState) {
                    is ViewModelState.LoadingState -> Unit
                    is ViewModelState.DataState -> vmState.data?.let { qrCode ->
                        qrCodeView?.setQrCode(qrCode)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.useVoucherEventsHelper.eventFlow.flowWithLifecycle(lifecycle)
                .collectLatest { sharedEvent ->
                    handleSharedEvent(sharedEvent)
                    redirectToLoginScreenFromSharedEvent(sharedEvent)
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.useVoucherEventsHelper.stateFlow.flowWithLifecycle(lifecycle)
                .collect { vmState ->
                    when (vmState) {
                        is ViewModelState.LoadingState -> Unit
                        is ViewModelState.DataState -> vmState.data?.let {
                            qrCodeView?.revealQrCode()
                        }
                    }
                }
        }
    }

    private fun bindViewToData() {
        viewModel.voucherCodeData?.let { voucherCodeData ->
            qrCodeView?.fillViewWithData(
                QrCodeViewAdapter(
                    color = viewModel.categoryUI?.categoryColor ?: Color.TRANSPARENT,
                    iconName = viewModel.categoryUI?.categoryIcon,
                    title = voucherCodeData.productLabel.orEmpty(),
                    creationDate = getString(
                        R.string.created_in,
                        parseToLongDate(voucherCodeData.voucherDate)
                    ),
                    expiryDate = getString(
                        R.string.expiry_date_value,
                        parseToLongDate(voucherCodeData.voucherExpiryDate)
                    )
                )
            )
        }
    }

    private val qrCodeViewCallback: QrCodeViewCallback by lazy {
        object : QrCodeViewCallback {
            override fun onBackButtonClicked() {
                dismissAllowingStateLoss()
            }

            override fun onUseVoucherButtonClicked() {
                viewModel.callUseVoucher()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        qrCodeView?.isQrCodeRevealed()?.let { isQrCodeRevealed ->
            if (isQrCodeRevealed) {
                handleDialogClosed?.invoke()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        qrCodeView = null
    }
}