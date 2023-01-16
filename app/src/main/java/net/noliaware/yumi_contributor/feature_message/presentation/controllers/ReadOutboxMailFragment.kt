package net.noliaware.yumi_contributor.feature_message.presentation.controllers

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
import net.noliaware.yumi_contributor.commun.util.*
import net.noliaware.yumi_contributor.feature_message.domain.model.Message
import net.noliaware.yumi_contributor.feature_message.presentation.views.ReadMailView

@AndroidEntryPoint
class ReadOutboxMailFragment : AppCompatDialogFragment() {

    companion object {
        fun newInstance(messageId: String): ReadOutboxMailFragment =
            ReadOutboxMailFragment().withArgs(MESSAGE_ID to messageId)
    }

    private var readMailView: ReadMailView? = null
    private val viewModel by viewModels<ReadOutboxMailFragmentViewModel>()

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

            override fun onComposeButtonClicked() = Unit
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
                R.string.sent_at,
                parseToLongDate(message.messageDate),
                parseTimeString(message.messageTime)
            ),
            message = message.messageBody.orEmpty()
        ).also {
            readMailView?.fillViewWithData(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        readMailView = null
    }
}