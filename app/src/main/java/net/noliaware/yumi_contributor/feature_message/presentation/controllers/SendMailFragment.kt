package net.noliaware.yumi_contributor.feature_message.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.FragmentKeys.REFRESH_SENT_MESSAGES_REQUEST_KEY
import net.noliaware.yumi_contributor.commun.domain.model.Priority
import net.noliaware.yumi_contributor.commun.presentation.mappers.PriorityMapper
import net.noliaware.yumi_contributor.commun.util.ViewState.DataState
import net.noliaware.yumi_contributor.commun.util.ViewState.LoadingState
import net.noliaware.yumi_contributor.commun.util.collectLifecycleAware
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.navDismiss
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.commun.util.toast
import net.noliaware.yumi_contributor.feature_message.presentation.adapters.MessagePriorityAdapter
import net.noliaware.yumi_contributor.feature_message.presentation.views.PriorityUI
import net.noliaware.yumi_contributor.feature_message.presentation.views.SendMailView
import net.noliaware.yumi_contributor.feature_message.presentation.views.SendMailView.SendMailViewCallback

@AndroidEntryPoint
class SendMailFragment : AppCompatDialogFragment() {

    private var sendMailView: SendMailView? = null
    private val args by navArgs<SendMailFragmentArgs>()
    private val viewModel by viewModels<SendMailFragmentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.send_mail_layout,
        container,
        false
    ).apply {
        sendMailView = this as SendMailView
        sendMailView?.callback = sendMailViewCallback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpPriorityDropdownView()
        setUpDefaultValuesIfAny()
        collectFlows()
    }

    private fun setUpPriorityDropdownView() {
        Priority.entries.map { priority ->
            val mapper = PriorityMapper()
            PriorityUI(
                resIcon = mapper.mapPriorityIcon(priority),
                label = getString(mapper.mapPriorityTitle(priority))
            )
        }.also { priorities ->
            sendMailView?.prioritySpinner?.adapter = MessagePriorityAdapter(
                requireContext(),
                priorities
            )
        }
    }

    private fun setUpDefaultValuesIfAny() {
        args.message?.let { selectedMessage ->
            selectedMessage.messageSender?.let { messageSender ->
                sendMailView?.setRecipientFixed(messageSender)
            }
            sendMailView?.setSubjectFixed(selectedMessage.messageSubject)
            sendMailView?.setPriorityFixed(PriorityMapper().mapPriorityIcon(selectedMessage.messagePriority))
        }
        args.domainName?.let { domainName ->
            sendMailView?.setMailDomain(domainName)
        }
    }

    private fun collectFlows() {
        viewModel.messageSentEventsHelper.eventFlow.collectLifecycleAware(viewLifecycleOwner) { sharedEvent ->
            handleSharedEvent(sharedEvent)
            redirectToLoginScreenFromSharedEvent(sharedEvent)
        }
        viewModel.messageSentEventsHelper.stateFlow.collectLifecycleAware(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is LoadingState -> Unit
                is DataState -> viewState.data?.let { result ->
                    if (result) {
                        setFragmentResult(
                            REFRESH_SENT_MESSAGES_REQUEST_KEY,
                            bundleOf()
                        )
                        navDismiss()
                    }
                }
            }
        }
    }

    private val sendMailViewCallback: SendMailViewCallback by lazy {
        object : SendMailViewCallback {
            override fun onBackButtonClicked() {
                navDismiss()
            }

            override fun onClearButtonClicked() {
                sendMailView?.clearMail()
            }

            override fun onSendMailClicked(
                recipients: List<String>,
                subject: String,
                text: String
            ) {
                args.message?.let {
                    sendMailReply(text)
                } ?: sendNewMail(
                    recipients = recipients,
                    subject = subject,
                    priority = Priority.entries[sendMailView?.getSelectedPriorityIndex() ?: 0].value,
                    text = text
                )
            }
        }
    }

    private fun sendMailReply(text: String) {
        viewModel.callSendMessage(
            messageId = args.message?.messageId,
            messageBody = text
        )
    }

    private fun sendNewMail(
        recipients: List<String>,
        subject: String,
        priority: Int,
        text: String
    ) {
        when {
            recipients.isEmpty() -> R.string.recipient_empty_error
            subject.isEmpty() -> R.string.subject_empty_error
            text.isEmpty() -> R.string.empty_mail_body_error
            else -> null
        }?.let { messageRes ->
            context.toast(messageRes, Toast.LENGTH_SHORT)
            return
        }
        viewModel.callSendMessage(
            recipients = recipients,
            subject = subject,
            messagePriority = priority,
            messageBody = text
        )
    }

    override fun onResume() {
        super.onResume()
        sendMailView?.computeMailView()
    }

    override fun onDestroyView() {
        sendMailView?.callback = null
        sendMailView = null
        super.onDestroyView()
    }
}