package net.noliaware.yumi_contributor.feature_message.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.READ_MESSAGE_FRAGMENT_TAG
import net.noliaware.yumi_contributor.commun.presentation.adapters.ListLoadStateAdapter
import net.noliaware.yumi_contributor.commun.util.handlePaginationError
import net.noliaware.yumi_contributor.feature_message.presentation.adapters.MessageAdapter
import net.noliaware.yumi_contributor.feature_message.presentation.views.MessagesListView

@AndroidEntryPoint
class SentMessagesFragment : Fragment() {

    private var messagesListView: MessagesListView? = null
    private val viewModel by viewModels<SentMessagesFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.messages_list_layout, container, false).apply {
            messagesListView = this as MessagesListView
            messagesListView?.messageAdapter = MessageAdapter { message ->
                ReadOutboxMailFragment.newInstance(
                    message.messageId
                ).apply {
                    onSentMessageListRefreshed = {
                        refreshAdapter()
                    }
                }.show(
                    childFragmentManager.beginTransaction(),
                    READ_MESSAGE_FRAGMENT_TAG
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFlows()
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            messagesListView?.messageAdapter?.loadStateFlow?.collectLatest { loadState ->
                if (loadState.refresh is LoadState.NotLoading) {
                    messagesListView?.setEmptyMessageText(getString(R.string.no_sent_message))
                    val alertsCount = messagesListView?.messageAdapter?.itemCount ?: 0
                    messagesListView?.setEmptyMessageVisible(alertsCount < 1)
                }
                handlePaginationError(loadState)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getMessages().collectLatest {
                messagesListView?.messageAdapter?.withLoadStateFooter(
                    footer = ListLoadStateAdapter()
                )
                messagesListView?.messageAdapter?.submitData(it)
            }
        }
    }

    fun refreshAdapter() {
        messagesListView?.messageAdapter?.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        messagesListView = null
    }
}