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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.DateTime
import net.noliaware.yumi_contributor.commun.FragmentKeys.REFRESH_VOUCHER_STATUS_REQUEST_KEY
import net.noliaware.yumi_contributor.commun.util.DecoratedText
import net.noliaware.yumi_contributor.commun.util.ViewState.DataState
import net.noliaware.yumi_contributor.commun.util.ViewState.LoadingState
import net.noliaware.yumi_contributor.commun.util.collectLifecycleAware
import net.noliaware.yumi_contributor.commun.util.decorateWords
import net.noliaware.yumi_contributor.commun.util.getFontFromResources
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.navDismiss
import net.noliaware.yumi_contributor.commun.util.parseDateToFormat
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.feature_account.presentation.views.QrCodeView
import net.noliaware.yumi_contributor.feature_account.presentation.views.QrCodeView.QrCodeViewAdapter
import net.noliaware.yumi_contributor.feature_account.presentation.views.QrCodeView.QrCodeViewCallback

@AndroidEntryPoint
class QrCodeFragment : AppCompatDialogFragment() {

    private var qrCodeView: QrCodeView? = null
    private val args by navArgs<QrCodeFragmentArgs>()
    private val viewModel by viewModels<QrCodeFragmentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.qr_code_layout,
        container,
        false
    ).apply {
        qrCodeView = this as QrCodeView
        qrCodeView?.callback = qrCodeViewCallback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFlows()
        bindViewToData()
    }

    private fun collectFlows() {
        viewModel.qrCodeStateFlow.collectLifecycleAware(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is LoadingState -> Unit
                is DataState -> viewState.data?.let { qrCode ->
                    qrCodeView?.setQrCode(qrCode)
                }
            }
        }
        viewModel.useVoucherEventsHelper.eventFlow.collectLifecycleAware(viewLifecycleOwner) { sharedEvent ->
            handleSharedEvent(sharedEvent)
            redirectToLoginScreenFromSharedEvent(sharedEvent)
        }
        viewModel.useVoucherEventsHelper.stateFlow.collectLifecycleAware(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is LoadingState -> Unit
                is DataState -> viewState.data?.let {
                    qrCodeView?.revealQrCode()
                }
            }
        }
    }

    private fun bindViewToData() {
        args.voucherCodeData.let { voucherCodeData ->
            qrCodeView?.fillViewWithData(
                QrCodeViewAdapter(
                    color = args.categoryUI.categoryColor,
                    iconName = args.categoryUI.categoryIcon,
                    title = voucherCodeData.productLabel.orEmpty(),
                    date = mapCodeDate(voucherCodeData.voucherDate)
                )
            )
        }
    }

    private fun mapCodeDate(dateStr: String): SpannableString {
        val expiryDate = dateStr.parseDateToFormat(DateTime.SHORT_DATE_FORMAT)
        return getString(
            R.string.usable_end_date,
            expiryDate
        ).decorateWords(
            wordsToDecorate = listOf(
                DecoratedText(
                    textToDecorate = expiryDate,
                    typeface = context?.getFontFromResources(R.font.omnes_semibold_regular)
                )
            )
        )
    }

    private val qrCodeViewCallback: QrCodeViewCallback by lazy {
        object : QrCodeViewCallback {
            override fun onBackButtonClicked() {
                navDismiss()
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
                setFragmentResult(
                    REFRESH_VOUCHER_STATUS_REQUEST_KEY,
                    bundleOf()
                )
            }
        }
    }

    override fun onDestroyView() {
        qrCodeView?.callback = null
        qrCodeView = null
        super.onDestroyView()
    }
}