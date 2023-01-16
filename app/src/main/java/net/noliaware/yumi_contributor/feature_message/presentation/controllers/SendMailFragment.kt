package net.noliaware.yumi_contributor.feature_message.presentation.controllers

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.R.style
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.MESSAGE_ID
import net.noliaware.yumi_contributor.commun.MESSAGE_SUBJECTS_DATA
import net.noliaware.yumi_contributor.commun.MESSAGE_SUBJECT_LABEL
import net.noliaware.yumi_contributor.commun.util.ViewModelState
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.commun.util.withArgs
import net.noliaware.yumi_contributor.feature_login.domain.model.MessageSubject
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
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.messageSentEventsHelper.eventFlow.collectLatest { sharedEvent ->
                handleSharedEvent(sharedEvent)
                redirectToLoginScreenFromSharedEvent(sharedEvent)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.messageSentEventsHelper.stateFlow.collectLatest { vmState ->
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

                if (dialog?.isShowing == true)
                    return

                // setup the alert builder
                val builder = MaterialAlertDialogBuilder(
                    requireContext(),
                    style.ThemeOverlay_Material3_Dialog
                )

                builder.setTitle(R.string.select_subject)

                viewModel.messageSubjects?.map {
                    it.subjectLabel
                }?.toTypedArray()?.let { messageSubjects ->
                    builder.setItems(messageSubjects) { _, which ->
                        selectedSubjectIndex = which
                        sendMailView?.setSubject(messageSubjects[which])
                    }
                }

                dialog = builder.create()
                dialog?.show()
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