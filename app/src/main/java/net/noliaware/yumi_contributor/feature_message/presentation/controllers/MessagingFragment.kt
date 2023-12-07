package net.noliaware.yumi_contributor.feature_message.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.FragmentKeys.REFRESH_RECEIVED_MESSAGES_REQUEST_KEY
import net.noliaware.yumi_contributor.commun.FragmentKeys.REFRESH_SENT_MESSAGES_REQUEST_KEY
import net.noliaware.yumi_contributor.commun.util.safeNavigate
import net.noliaware.yumi_contributor.feature_message.presentation.views.MessagingView
import net.noliaware.yumi_contributor.feature_message.presentation.views.MessagingView.*

@AndroidEntryPoint
class MessagingFragment : Fragment() {

    private var messagingView: MessagingView? = null
    private val args by navArgs<MessagingFragmentArgs>()
    private val viewModel by viewModels<MessagingFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.messaging_layout,
        container,
        false
    )?.apply {
        messagingView = this as MessagingView
        messagingView?.callback = messagingViewCallback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpFragmentListener()
        MessageFragmentStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle).apply {
            messagingView?.getViewPager?.adapter = this
        }
    }

    private fun setUpFragmentListener() {
        setFragmentResultListener(
            REFRESH_RECEIVED_MESSAGES_REQUEST_KEY
        ) { _, _ ->
            viewModel.sendReceivedListRefreshedEvent()
        }
        setFragmentResultListener(
            REFRESH_SENT_MESSAGES_REQUEST_KEY
        ) { _, _ ->
            viewModel.sendSentListRefreshedEvent()
        }
    }

    private val messagingViewCallback: MailViewCallback by lazy {
        MailViewCallback {
            findNavController().safeNavigate(
                MessagingFragmentDirections.actionMessagingFragmentToSendMailFragment(args.domainName)
            )
        }
    }

    override fun onDestroyView() {
        messagingView = null
        super.onDestroyView()
    }

    private class MessageFragmentStateAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount() = 2
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ReceivedMessagesFragment()
                else -> SentMessagesFragment()
            }
        }
    }
}