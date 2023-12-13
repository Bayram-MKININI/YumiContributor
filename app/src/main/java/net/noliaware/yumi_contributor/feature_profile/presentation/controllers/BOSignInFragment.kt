package net.noliaware.yumi_contributor.feature_profile.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.ViewState.DataState
import net.noliaware.yumi_contributor.commun.util.ViewState.LoadingState
import net.noliaware.yumi_contributor.commun.util.collectLifecycleAware
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
    ): View? = inflater.inflate(
        R.layout.bo_sign_in_layout,
        container,
        false
    ).apply {
        boSignInView = this as BOSignInParentView
        boSignInView?.callback = boSignInViewCallback
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
        viewModel.eventsHelper.eventFlow.collectLifecycleAware(viewLifecycleOwner) { sharedEvent ->
            handleSharedEvent(sharedEvent)
            redirectToLoginScreenFromSharedEvent(sharedEvent)
        }
        viewModel.eventsHelper.stateFlow.collectLifecycleAware(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is LoadingState -> Unit
                is DataState -> viewState.data?.let { boSignIn ->
                    boSignInView?.getBoSignInView?.displayCode(boSignIn.signInCode)
                    viewModel.startTimerWithPeriod(boSignIn.expiryDelayInSeconds)
                }
            }
        }
        viewModel.timerStateFlow.collectLifecycleAware(viewLifecycleOwner) { timerState ->
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

    override fun onDestroyView() {
        boSignInView?.callback = null
        boSignInView = null
        super.onDestroyView()
    }
}