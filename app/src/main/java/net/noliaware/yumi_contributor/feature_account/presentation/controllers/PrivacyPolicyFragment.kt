package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.ViewState.DataState
import net.noliaware.yumi_contributor.commun.util.ViewState.LoadingState
import net.noliaware.yumi_contributor.commun.util.collectLifecycleAware
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.isNetworkReachable
import net.noliaware.yumi_contributor.commun.util.navDismiss
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.feature_account.presentation.views.PrivacyPolicyView
import net.noliaware.yumi_contributor.feature_account.presentation.views.PrivacyPolicyView.PrivacyPolicyViewCallback

@AndroidEntryPoint
class PrivacyPolicyFragment : AppCompatDialogFragment() {

    private var privacyPolicyView: PrivacyPolicyView? = null
    private val args by navArgs<PrivacyPolicyFragmentArgs>()
    private val viewModel by viewModels<PrivacyPolicyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogTheme)
        isCancelable = !args.isPrivacyPolicyConfirmationRequired
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.privacy_policy_layout,
        container,
        false
    ).apply {
        privacyPolicyView = this as PrivacyPolicyView
        privacyPolicyView?.callback = privacyPolicyViewCallback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFlows()
        if (isNetworkReachable(requireContext())) {
            privacyPolicyView?.showWebView()
            privacyPolicyView?.loadURL(args.privacyPolicyUrl)
        } else {
            privacyPolicyView?.showOfflineMessage()
        }
    }

    private fun collectFlows() {
        viewModel.privacyPolicyStatusEventsHelper.eventFlow.collectLifecycleAware(viewLifecycleOwner) { sharedEvent ->
            handleSharedEvent(sharedEvent)
            redirectToLoginScreenFromSharedEvent(sharedEvent)
        }
        viewModel.privacyPolicyStatusEventsHelper.stateFlow.collectLifecycleAware(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is LoadingState -> Unit
                is DataState -> viewState.data?.let { result ->
                    if (result) {
                        navDismiss()
                    }
                }
            }
        }
    }

    private val privacyPolicyViewCallback: PrivacyPolicyViewCallback by lazy {
        PrivacyPolicyViewCallback {
            if (args.isPrivacyPolicyConfirmationRequired) {
                viewModel.callUpdatePrivacyPolicyReadStatus()
            } else {
                navDismiss()
            }
        }
    }

    override fun onDestroyView() {
        privacyPolicyView?.destroyWebPage()
        privacyPolicyView?.callback = null
        privacyPolicyView = null
        super.onDestroyView()
    }
}