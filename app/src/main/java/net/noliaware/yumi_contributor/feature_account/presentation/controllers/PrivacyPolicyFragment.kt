package net.noliaware.yumi_contributor.feature_account.presentation.controllers

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
import net.noliaware.yumi_contributor.commun.Args.PRIVACY_POLICY_CONFIRMATION_REQUIRED
import net.noliaware.yumi_contributor.commun.Args.PRIVACY_POLICY_URL
import net.noliaware.yumi_contributor.commun.util.ViewModelState
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.isNetworkReachable
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.commun.util.withArgs
import net.noliaware.yumi_contributor.feature_account.presentation.views.PrivacyPolicyView

@AndroidEntryPoint
class PrivacyPolicyFragment : AppCompatDialogFragment() {

    companion object {
        fun newInstance(
            privacyPolicyUrl: String,
            isConfirmationRequired: Boolean
        ) = PrivacyPolicyFragment().withArgs(
            PRIVACY_POLICY_URL to privacyPolicyUrl,
            PRIVACY_POLICY_CONFIRMATION_REQUIRED to isConfirmationRequired
        )
    }

    private var privacyPolicyView: PrivacyPolicyView? = null
    private val viewModel by viewModels<PrivacyPolicyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogTheme)
        isCancelable = !viewModel.privacyPolicyConfirmationRequired
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.privacy_policy_layout,
            container,
            false
        ).apply {
            privacyPolicyView = this as PrivacyPolicyView
            privacyPolicyView?.callback = privacyPolicyViewCallback
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFlows()
        if (isNetworkReachable(requireContext())) {
            privacyPolicyView?.showWebView()
            privacyPolicyView?.loadURL(viewModel.privacyPolicyUrl)
        } else {
            privacyPolicyView?.showOfflineMessage()
        }
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.privacyPolicyStatusEventsHelper.eventFlow.collectLatest { sharedEvent ->
                handleSharedEvent(sharedEvent)
                redirectToLoginScreenFromSharedEvent(sharedEvent)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.privacyPolicyStatusEventsHelper.stateFlow.collectLatest { vmState ->
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

    private val privacyPolicyViewCallback: PrivacyPolicyView.PrivacyPolicyViewCallback by lazy {
        PrivacyPolicyView.PrivacyPolicyViewCallback {
            if (viewModel.privacyPolicyConfirmationRequired) {
                viewModel.callUpdatePrivacyPolicyReadStatus()
            } else {
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        privacyPolicyView?.destroyWebPage()
        super.onDestroyView()
    }
}