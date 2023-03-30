package net.noliaware.yumi_contributor.feature_message.presentation.controllers

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.MESSAGE_ID
import net.noliaware.yumi_contributor.commun.MESSAGE_SUBJECTS_DATA
import net.noliaware.yumi_contributor.commun.MESSAGE_SUBJECT_LABEL
import net.noliaware.yumi_contributor.commun.util.ViewModelState
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.commun.util.withArgs
import net.noliaware.yumi_contributor.feature_login.domain.model.MessageSubject
import net.noliaware.yumi_contributor.feature_message.presentation.views.MessageSubjectItemView
import net.noliaware.yumi_contributor.feature_message.presentation.views.MessageSubjectsListView
import net.noliaware.yumi_contributor.feature_message.presentation.views.SendMailView

@AndroidEntryPoint
class SendMailFragment : AppCompatDialogFragment() {

    companion object {
        fun newInstance(
            messageSubjects: List<MessageSubject>? = null,
            messageId: String? = null,
            messageSubjectLabel: String? = null
        ) = SendMailFragment().withArgs(
            MESSAGE_SUBJECTS_DATA to messageSubjects,
            MESSAGE_ID to messageId,
            MESSAGE_SUBJECT_LABEL to messageSubjectLabel
        )
    }

    private var sendMailView: SendMailView? = null
    private val viewModel by viewModels<SendMailFragmentViewModel>()
    private var dialog: AlertDialog? = null
    private var selectedSubjectIndex: Int = 0
    var onMessageSent: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.send_mail_layout, container, false).apply {
            sendMailView = this as SendMailView
            sendMailView?.callback = sendMailViewCallback
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.messageSubjectLabel?.let { sendMailView?.setSubjectFixed(it) }
        collectFlows()
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.messageSentEventsHelper.eventFlow.flowWithLifecycle(lifecycle)
                .collectLatest { sharedEvent ->
                    handleSharedEvent(sharedEvent)
                    redirectToLoginScreenFromSharedEvent(sharedEvent)
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.messageSentEventsHelper.stateFlow.flowWithLifecycle(lifecycle)
                .collectLatest { vmState ->
                    when (vmState) {
                        is ViewModelState.LoadingState -> Unit
                        is ViewModelState.DataState -> vmState.data?.let { result ->
                            if (result) {
                                dismiss()
                            }
                        }
                    }
                }
        }
    }

    private val sendMailViewCallback: SendMailView.SendMailViewCallback by lazy {
        object : SendMailView.SendMailViewCallback {
            override fun onBackButtonClicked() {
                dismissAllowingStateLoss()
            }

            override fun onSubjectEditTextClicked() {

                if (dialog?.isShowing == true) {
                    return
                }

                MaterialAlertDialogBuilder(
                    requireContext()
                ).apply {

                    val messageSubjectsListView = layoutInflater.inflate(
                        R.layout.message_subjects_list_layout,
                        null
                    ) as MessageSubjectsListView

                    viewModel.messageSubjects?.mapIndexed { index, messageSubject ->
                        MessageSubjectItemView.MessageSubjectItemViewAdapter(
                            subject = messageSubject.subjectLabel,
                            backgroundDrawable = if (index % 2 == 0) {
                                R.drawable.rectangle_white_ripple
                            } else {
                                R.drawable.rectangle_grey7_ripple
                            }
                        )
                    }?.let {
                        messageSubjectsListView.fillViewWithData(it)
                    }

                    messageSubjectsListView.setMessageSubjectsListViewCallback(object :
                        MessageSubjectsListView.MessageSubjectsListViewCallback {
                        override fun onSubjectClickedAtIndex(index: Int) {
                            viewModel.messageSubjects?.get(index)?.let {
                                sendMailView?.setSubject(it.subjectLabel)
                                dialog?.dismiss()
                            }
                        }

                        override fun onClickOutside() {
                            dialog?.dismiss()
                        }
                    })

                    setView(messageSubjectsListView)
                    dialog = create()
                    dialog?.show()
                    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                }
            }

            override fun onClearButtonClicked() {
                sendMailView?.clearMail()
            }

            override fun onSendMailClicked(text: String) {
                viewModel.messageSubjects?.get(selectedSubjectIndex)?.let { messageSubject ->
                    viewModel.callSendMessage(
                        messagePriority = 1,
                        messageSubjectId = messageSubject.subjectId.toString(),
                        messageBody = text
                    )
                }

                viewModel.messageId?.let { messageId ->
                    viewModel.callSendMessage(
                        messagePriority = 1,
                        messageId = messageId,
                        messageBody = text
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        sendMailView?.computeMailView()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.messageSentEventsHelper.stateData?.let { messageSent ->
            if (messageSent) {
                onMessageSent?.invoke()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sendMailView = null
    }
}