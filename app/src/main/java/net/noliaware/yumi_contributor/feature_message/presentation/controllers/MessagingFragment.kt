package net.noliaware.yumi_contributor.feature_message.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.MESSAGE_SUBJECTS_DATA
import net.noliaware.yumi_contributor.commun.SEND_MESSAGES_FRAGMENT_TAG
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.withArgs
import net.noliaware.yumi_contributor.feature_login.domain.model.MessageSubject
import net.noliaware.yumi_contributor.feature_message.presentation.views.MessagingView

@AndroidEntryPoint
class MessagingFragment : Fragment() {

    companion object {
        fun newInstance(messageSubjects: List<MessageSubject>?) =
            MessagingFragment().withArgs(MESSAGE_SUBJECTS_DATA to messageSubjects)
    }

    private var messagingView: MessagingView? = null
    private val viewModel by viewModels<MessagingFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return container?.inflate(R.layout.messaging_layout, false)?.apply {
            messagingView = this as MessagingView
            messagingView?.callback = messagingViewCallback
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val viewPager = messagingView?.getViewPager
        val tabLayout = messagingView?.getTabLayout

        MessageFragmentStateAdapter(childFragmentManager, lifecycle).apply {
            viewPager?.adapter = this
        }

        TabLayoutMediator(tabLayout!!, viewPager!!) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.received)
                else -> getString(R.string.sent)
            }
        }.attach()
    }

    private val messagingViewCallback: MessagingView.MailViewCallback by lazy {
        object : MessagingView.MailViewCallback {
            override fun onComposeButtonClicked() {
                SendMailFragment.newInstance(
                    viewModel.messageSubjects ?: listOf()
                ).apply {
                    onMessageSent = {
                        (messagingView?.getViewPager?.adapter as MessageFragmentStateAdapter).refreshSentFragment()
                    }
                }.show(
                    childFragmentManager.beginTransaction(), SEND_MESSAGES_FRAGMENT_TAG
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        messagingView = null
    }
}

class MessageFragmentStateAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    var fragments = Array<Fragment?>(2) { null }

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                fragments[0] = ReceivedMessagesFragment()
                fragments[0]!!
            }
            else -> {
                fragments[1] = SentMessagesFragment()
                fragments[1]!!
            }
        }
    }

    fun refreshSentFragment() {
        (fragments[1] as? SentMessagesFragment)?.refreshAdapter()
    }
}