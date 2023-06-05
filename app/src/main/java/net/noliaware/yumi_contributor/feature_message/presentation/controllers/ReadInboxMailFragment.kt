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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.HOURS_TIME_FORMAT
import net.noliaware.yumi_contributor.commun.LONG_DATE_WITH_DAY_FORMAT
import net.noliaware.yumi_contributor.commun.MESSAGE_ID
import net.noliaware.yumi_contributor.commun.MESSAGE_PRIORITY
import net.noliaware.yumi_contributor.commun.MESSAGE_SUBJECT_LABEL
import net.noliaware.yumi_contributor.commun.SEND_MESSAGES_FRAGMENT_TAG
import net.noliaware.yumi_contributor.commun.presentation.mappers.PriorityMapper
import net.noliaware.yumi_contributor.commun.util.ViewModelState
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.parseDateToFormat
import net.noliaware.yumi_contributor.commun.util.parseTimeToFormat
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.commun.util.withArgs
import net.noliaware.yumi_contributor.feature_message.domain.model.Message
import net.noliaware.yumi_contributor.feature_message.presentation.views.ReadMailView
import net.noliaware.yumi_contributor.feature_message.presentation.views.ReadMailView.ReadMailViewAdapter
import net.noliaware.yumi_contributor.feature_message.presentation.views.ReadMailView.ReadMailViewCallback

@AndroidEntryPoint
class ReadInboxMailFragment : AppCompatDialogFragment() {

    companion object {
        fun newInstance(
            messageId: String,
            messageSubjectLabel: String? = null,
            messagePriority: Int? = null
        ) = ReadInboxMailFragment().withArgs(
            MESSAGE_ID to messageId,
            MESSAGE_SUBJECT_LABEL to messageSubjectLabel,
            MESSAGE_PRIORITY to messagePriority
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFlows()
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getMessageEventsHelper.eventFlow.collectLatest { sharedEvent ->
                    handleSharedEvent(sharedEvent)
                    redirectToLoginScreenFromSharedEvent(sharedEvent)
                }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getMessageEventsHelper.stateFlow.collect { vmState ->
                    when (vmState) {
                        is ViewModelState.LoadingState -> Unit
                        is ViewModelState.DataState -> vmState.data?.let { message ->
                            bindViewToData(message)
                        }
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.deleteMessageEventsHelper.stateFlow.collect { vmState ->
                    when (vmState) {
                        is ViewModelState.LoadingState -> Unit
                        is ViewModelState.DataState -> vmState.data?.let { result ->
                            if (result) {
                                viewModel.receivedMessageListShouldRefresh = true
                                dismissAllowingStateLoss()
                            }
                        }
                    }
                }
        }
    }

    private fun bindViewToData(message: Message) {
        ReadMailViewAdapter(
            priorityIconRes = PriorityMapper().mapPriorityIcon(message.messagePriority),
            subject = if (message.messageType.isNullOrEmpty()) {
                message.messageSubject
            } else {
                "${message.messageType} ${message.messageSubject}"
            },
            time = getString(
                R.string.received_at,
                message.messageDate.parseDateToFormat(LONG_DATE_WITH_DAY_FORMAT),
                message.messageTime.parseTimeToFormat(HOURS_TIME_FORMAT)
            ),
            message = message.messageBody.orEmpty(),
            replyPossible = true
        ).also {
            readMailView?.fillViewWithData(it)
        }
    }


    private val readMailViewCallback: ReadMailViewCallback by lazy {
        object : ReadMailViewCallback {
            override fun onBackButtonClicked() {
                dismissAllowingStateLoss()
            }

            override fun onDeleteButtonClicked() {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.delete)
                    .setMessage(R.string.delete_mail_confirmation)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        dialog.dismiss()
                        viewModel.callDeleteInboxMessageForId()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }

            override fun onComposeButtonClicked() {
                SendMailFragment.newInstance(
                    message = viewModel.getMessageEventsHelper.stateData
                ).apply {
                    onMessageSent = { viewModel.receivedMessageListShouldRefresh = true }
                }.show(
                    childFragmentManager.beginTransaction(), SEND_MESSAGES_FRAGMENT_TAG
                )
            }
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