package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.FragmentKeys.REFRESH_VOUCHER_DETAILS_REQUEST_KEY
import net.noliaware.yumi_contributor.commun.util.DecoratedText
import net.noliaware.yumi_contributor.commun.util.ViewState
import net.noliaware.yumi_contributor.commun.util.collectLifecycleAware
import net.noliaware.yumi_contributor.commun.util.decorateWords
import net.noliaware.yumi_contributor.commun.util.getColorCompat
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.navDismiss
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherRequest
import net.noliaware.yumi_contributor.feature_account.presentation.adapters.VoucherOngoingRequestsAdapter
import net.noliaware.yumi_contributor.feature_account.presentation.views.VoucherOngoingRequestListView
import net.noliaware.yumi_contributor.feature_account.presentation.views.VoucherOngoingRequestListView.VoucherOngoingRequestListViewCallback

@AndroidEntryPoint
class VoucherOngoingRequestListFragment : AppCompatDialogFragment() {

    private var requestsListView: VoucherOngoingRequestListView? = null
    private val args by navArgs<VoucherOngoingRequestListFragmentArgs>()
    private val viewModel by viewModels<VoucherOngoingRequestListFragmentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.voucher_ongoing_request_list_layout,
        container,
        false
    ).apply {
        requestsListView = this as VoucherOngoingRequestListView
        requestsListView?.callback = voucherOngoingRequestListViewCallback
        requestsListView?.voucherOngoingRequestAdapter = VoucherOngoingRequestsAdapter { voucherRequest ->
                displayDeleteRequestDialog(voucherRequest)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = getString(R.string.requests_in_progress)
        requestsListView?.setUpViewLook(
            color = args.categoryUI.categoryColor,
            iconName = args.categoryUI.categoryIcon,
            title = title.decorateWords(
                wordsToDecorate = listOf(
                    DecoratedText(
                        textToDecorate = getString(R.string.in_progress_highlighted_text).lowercase(),
                        color = context?.getColorCompat(R.color.colorPrimary) ?: Color.TRANSPARENT
                    )
                )
            )
        )
        requestsListView?.setLoadingVisible(true)
        collectFlows()
    }

    private fun collectFlows() {
        viewModel.getVoucherRequestsEventsHelper.eventFlow.collectLifecycleAware(viewLifecycleOwner) { sharedEvent ->
            requestsListView?.stopLoading()
            handleSharedEvent(sharedEvent)
            redirectToLoginScreenFromSharedEvent(sharedEvent)
        }
        viewModel.getVoucherRequestsEventsHelper.stateFlow.collectLifecycleAware(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is ViewState.LoadingState -> Unit
                is ViewState.DataState -> viewState.data?.let { requests ->
                    requestsListView?.setLoadingVisible(false)
                    requestsListView?.voucherOngoingRequestAdapter?.submitList(requests)
                    requestsListView?.setEmptyMessageVisible(requests.isEmpty())
                }
            }
        }

        viewModel.deleteVoucherRequestEventsHelper.stateFlow.collectLifecycleAware(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is ViewState.LoadingState -> Unit
                is ViewState.DataState -> viewState.data?.let { result ->
                    if (result) {
                        viewModel.callGetVoucherRequestList()
                    }
                }
            }
        }
    }

    private fun displayDeleteRequestDialog(voucherRequest: VoucherRequest) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete)
            .setMessage(R.string.delete_voucher_request_confirmation)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                viewModel.callRemoveVoucherRequestById(voucherRequest.voucherRequestId)
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private val voucherOngoingRequestListViewCallback: VoucherOngoingRequestListViewCallback by lazy {
        VoucherOngoingRequestListViewCallback {
            navDismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (viewModel.getVoucherRequestsEventsHelper.stateData?.isEmpty() == true) {
            setFragmentResult(
                REFRESH_VOUCHER_DETAILS_REQUEST_KEY,
                bundleOf()
            )
        }
    }

    override fun onDestroyView() {
        requestsListView = null
        super.onDestroyView()
    }
}