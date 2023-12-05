package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.DateTime.SHORT_DATE_FORMAT
import net.noliaware.yumi_contributor.commun.util.DecoratedText
import net.noliaware.yumi_contributor.commun.util.ViewState
import net.noliaware.yumi_contributor.commun.util.collectLifecycleAware
import net.noliaware.yumi_contributor.commun.util.decorateWords
import net.noliaware.yumi_contributor.commun.util.getColorCompat
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.navDismiss
import net.noliaware.yumi_contributor.commun.util.parseDateToFormat
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherRequest
import net.noliaware.yumi_contributor.feature_account.presentation.views.VoucherOngoingRequestItemView.VoucherOngoingRequestItemViewAdapter
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
                    bindViewToData(requests)
                }
            }
        }
    }

    private fun bindViewToData(requests: List<VoucherRequest>) {
        requestsListView?.fillViewWithData(
            requests.map { voucherRequest ->
                VoucherOngoingRequestItemViewAdapter(
                    title = voucherRequest.voucherRequestLabel.orEmpty(),
                    date = getString(
                        R.string.date_time,
                        voucherRequest.voucherRequestDate?.parseDateToFormat(SHORT_DATE_FORMAT),
                        voucherRequest.voucherRequestTime
                    ),
                    body = voucherRequest.voucherRequestComment.orEmpty()
                )
            }
        )
    }

    private val voucherOngoingRequestListViewCallback: VoucherOngoingRequestListViewCallback by lazy {
        VoucherOngoingRequestListViewCallback {
            navDismiss()
        }
    }

    override fun onDestroyView() {
        requestsListView = null
        super.onDestroyView()
    }
}