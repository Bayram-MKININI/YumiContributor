package net.noliaware.yumi_contributor.feature_profile.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.BO_SIGN_IN_FRAGMENT_TAG
import net.noliaware.yumi_contributor.commun.util.ViewModelState
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.feature_profile.domain.model.UserProfile
import net.noliaware.yumi_contributor.feature_profile.presentation.views.ProfileParentView
import net.noliaware.yumi_contributor.feature_profile.presentation.views.ProfileParentView.ProfileParentViewCallback
import net.noliaware.yumi_contributor.feature_profile.presentation.views.ProfileView.ProfileViewAdapter

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    private var profileParentView: ProfileParentView? = null
    private val viewModel by viewModels<UserProfileFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return container?.inflate(R.layout.profile_layout, false)?.apply {
            profileParentView = this as ProfileParentView
            profileParentView?.callback = profileViewCallback
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFlows()
    }

    private fun collectFlows() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userProfileEventsHelper.eventFlow.collectLatest { sharedEvent ->
                handleSharedEvent(sharedEvent)
                redirectToLoginScreenFromSharedEvent(sharedEvent)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userProfileEventsHelper.stateFlow.collect { vmState ->
                when (vmState) {
                    is ViewModelState.LoadingState -> Unit
                    is ViewModelState.DataState -> vmState.data?.let { userProfile ->
                        bindViewToData(userProfile)
                    }
                }
            }
        }
    }

    private fun bindViewToData(userProfile: UserProfile) {
        val address = StringBuilder().apply {
            append(userProfile.address)
            append(getString(R.string.new_line))
            if (userProfile.addressComplement?.isNotBlank() == true) {
                append(userProfile.addressComplement)
                append(getString(R.string.new_line))
            }
            append(userProfile.postCode)
            append(" ")
            append(userProfile.city)
        }.toString()

        ProfileViewAdapter(
            login = userProfile.login.orEmpty(),
            surname = userProfile.lastName.orEmpty(),
            name = userProfile.firstName.orEmpty(),
            phone = userProfile.cellPhoneNumber.orEmpty(),
            address = address
        ).also {
            profileParentView?.getProfileView?.fillViewWithData(it)
        }
    }

    private val profileViewCallback: ProfileParentViewCallback by lazy {
        ProfileParentViewCallback {
            BOSignInFragment.newInstance().show(
                childFragmentManager.beginTransaction(),
                BO_SIGN_IN_FRAGMENT_TAG
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        profileParentView = null
    }
}