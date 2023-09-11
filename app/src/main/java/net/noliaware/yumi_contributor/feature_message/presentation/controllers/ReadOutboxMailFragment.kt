package net.noliaware.yumi_contributor.feature_message.presentation.controllers

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.ApiParameters.MESSAGE_ID
import net.noliaware.yumi_contributor.commun.DateTime.HOURS_TIME_FORMAT
import net.noliaware.yumi_contributor.commun.DateTime.LONG_DATE_WITH_DAY_FORMAT
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
class ReadOutboxMailFragment : AppCompatDialogFragment() {

    companion object {
        fun newInstance(
            messageId: String
        ) = ReadOutboxMailFragment().withArgs(MESSAGE_ID to messageId)
    }

    private var readMailView: ReadMailView? = null
    private val viewModel by viewModels<ReadOutboxMailFragmentViewModel>()
    var onSentMessageListRefreshed: (() -> Unit)? = null

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
                        viewModel.callDeleteOutboxMessageForId()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }

            override fun onComposeButtonClicked() = Unit
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
                    is ViewModelState.LoadingState -> readMailView?.setLoadingVisible(true)
                    is ViewModelState.DataState -> vmState.data?.let { message ->
                        readMailView?.setLoadingVisible(false)
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
                            viewModel.sentMessageListShouldRefresh = true
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
                R.string.sent_at,
                message.messageDate.parseDateToFormat(LONG_DATE_WITH_DAY_FORMAT),
                message.messageTime.parseTimeToFormat(HOURS_TIME_FORMAT)
            ),
            message = HtmlCompat.fromHtml(
                message.messageBody.orEmpty(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
        ).also {
            readMailView?.fillViewWithData(it)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (viewModel.sentMessageListShouldRefresh == true) {
            onSentMessageListRefreshed?.invoke()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        readMailView = null
    }
}