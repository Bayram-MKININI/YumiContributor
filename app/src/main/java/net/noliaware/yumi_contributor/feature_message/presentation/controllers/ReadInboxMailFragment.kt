package net.noliaware.yumi_contributor.feature_message.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.DateTime.HOURS_TIME_FORMAT
import net.noliaware.yumi_contributor.commun.DateTime.LONG_DATE_WITH_DAY_FORMAT
import net.noliaware.yumi_contributor.commun.FragmentKeys.REFRESH_RECEIVED_MESSAGES_REQUEST_KEY
import net.noliaware.yumi_contributor.commun.presentation.mappers.PriorityMapper
import net.noliaware.yumi_contributor.commun.util.ViewModelState
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.navDismiss
import net.noliaware.yumi_contributor.commun.util.parseDateToFormat
import net.noliaware.yumi_contributor.commun.util.parseTimeToFormat
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.commun.util.safeNavigate
import net.noliaware.yumi_contributor.feature_message.domain.model.Message
import net.noliaware.yumi_contributor.feature_message.presentation.views.ReadMailView
import net.noliaware.yumi_contributor.feature_message.presentation.views.ReadMailView.*

@AndroidEntryPoint
class ReadInboxMailFragment : AppCompatDialogFragment() {

    private var readMailView: ReadMailView? = null
    private val viewModel by viewModels<ReadInboxMailFragmentViewModel>()

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
                readMailView?.activateLoading(false)
                handleSharedEvent(sharedEvent)
                redirectToLoginScreenFromSharedEvent(sharedEvent)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getMessageEventsHelper.stateFlow.collect { vmState ->
                when (vmState) {
                    is ViewModelState.LoadingState -> readMailView?.activateLoading(true)
                    is ViewModelState.DataState -> vmState.data?.let { message ->
                        readMailView?.activateLoading(false)
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
                            setFragmentResult(
                                REFRESH_RECEIVED_MESSAGES_REQUEST_KEY,
                                bundleOf()
                            )
                            navDismiss()
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
            message = HtmlCompat.fromHtml(
                message.messageBody.orEmpty(),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            ),
            replyPossible = true
        ).also {
            readMailView?.fillViewWithData(it)
        }
    }

    private val readMailViewCallback: ReadMailViewCallback by lazy {
        object : ReadMailViewCallback {
            override fun onBackButtonClicked() {
                navDismiss()
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
                findNavController().safeNavigate(
                    ReadInboxMailFragmentDirections.actionReadInboxMailFragmentToSendMailFragment(
                        message = viewModel.getMessageEventsHelper.stateData
                    )
                )
            }
        }
    }

    override fun onDestroyView() {
        readMailView = null
        super.onDestroyView()
    }
}