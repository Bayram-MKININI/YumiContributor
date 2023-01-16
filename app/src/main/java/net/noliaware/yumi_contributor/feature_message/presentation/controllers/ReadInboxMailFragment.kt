package net.noliaware.yumi_contributor.feature_message.presentation.controllers

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
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.MESSAGE_ID
import net.noliaware.yumi_contributor.commun.MESSAGE_SUBJECT_LABEL
import net.noliaware.yumi_contributor.commun.SEND_MESSAGES_FRAGMENT_TAG
import net.noliaware.yumi_contributor.commun.util.*
import net.noliaware.yumi_contributor.feature_message.domain.model.Message
import net.noliaware.yumi_contributor.feature_message.presentation.views.ReadMailView

@AndroidEntryPoint
class ReadInboxMailFragment : AppCompatDialogFragment() {

    companion object {
        fun newInstance(
            messageId: String,
            messageSubjectLabel: String? = null
        ) = ReadInboxMailFragment().withArgs(
            MESSAGE_ID to messageId,
            MESSAGE_SUBJECT_LABEL to messageSubjectLabel
        )
    }

    private var readMailView: ReadMailView? = null
    private val viewModel by viewModels<ReadInboxMailFragmentViewModel>()
    var onReceivedMessageListRefreshed: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.read_mail_layout, container, false).apply {
            readMailView = this as ReadMailView
            readMailView?.callback = readMailViewCallback
        }
    }

    private val readMailViewCallback: ReadMailView.ReadMailViewCallback by lazy {
        object : ReadMailView.ReadMailViewCallback {
            override fun onBackButtonClicked() {
                dismissAllowingStateLoss()
            }

            override fun onComposeButtonClicked() {
                SendMailFragment.newInstance(
                    messageId = viewModel.messageId,
                    messageSubjectLabel = viewModel.messageSubjectLabel
                ).apply {
                    onMessageSent = { viewModel.receivedMessageListShouldRefresh = true }
                }.show(
                    childFragmentManager.beginTransaction(), SEND_MESSAGES_FRAGMENT_TAG
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
            viewModel.eventsHelper.eventFlow.collectLatest { sharedEvent ->
                handleSharedEvent(sharedEvent)
                redirectToLoginScreenFromSharedEvent(sharedEvent)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsHelper.stateFlow.collect { vmState ->
                when (vmState) {
                    is ViewModelState.LoadingState -> Unit
                    is ViewModelState.DataState -> vmState.data?.let { message ->
                        bindViewToData(message)
                    }
                }
            }
        }
    }

    private fun bindViewToData(message: Message) {
        ReadMailView.ReadMailViewAdapter(
            subject = message.messageSubject,
            time = getString(
                R.string.received_at,
                parseToLongDate(message.messageDate),
                parseTimeString(message.messageTime)
            ),
            message = message.messageBody.orEmpty(),
            replyPossible = true
        ).also {
            readMailView?.fillViewWithData(it)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (viewModel.receivedMessageListShouldRefresh == true) {
            onReceivedMessageListRefreshed?.invoke()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        readMailView = null
    }
}