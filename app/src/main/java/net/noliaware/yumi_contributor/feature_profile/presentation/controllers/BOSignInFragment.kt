package net.noliaware.yumi_contributor.feature_profile.presentation.controllers

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
import net.noliaware.yumi_contributor.commun.util.ViewModelState
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.navDismiss
import net.noliaware.yumi_contributor.commun.util.parseSecondsToMinutesString
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.feature_profile.presentation.views.BOSignInParentView
import net.noliaware.yumi_contributor.feature_profile.presentation.views.BOSignInParentView.BOSignInViewCallback

@AndroidEntryPoint
class BOSignInFragment : AppCompatDialogFragment() {

    private var boSignInView: BOSignInParentView? = null
    private val viewModel by viewModels<BOSignInFragmentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bo_sign_in_layout, container, false).apply {
            boSignInView = this as BOSignInParentView
            boSignInView?.callback = boSignInViewCallback
        }
    }

    private val boSignInViewCallback: BOSignInViewCallback by lazy {
        BOSignInViewCallback {
            navDismiss()
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
                    is ViewModelState.DataState -> vmState.data?.let { boSignIn ->
                        boSignInView?.getBoSignInView?.displayCode(boSignIn.signInCode)
                        viewModel.startTimerWithPeriod(boSignIn.expiryDelayInSeconds)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.timerStateFlow.collect { timerState ->
                boSignInView?.getBoSignInView?.displayRemainingTime(
                    timerState.secondsRemaining?.parseSecondsToMinutesString() ?: getString(R.string.empty_time)
                )
                timerState.secondsRemaining?.let { secondsRemaining ->
                    if (secondsRemaining <= 0) {
                        navDismiss()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        boSignInView = null
        super.onDestroyView()
    }
}