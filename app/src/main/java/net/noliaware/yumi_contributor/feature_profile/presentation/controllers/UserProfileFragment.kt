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
import net.noliaware.yumi_contributor.commun.Args.ACCOUNT_DATA
import net.noliaware.yumi_contributor.commun.FragmentTags.BO_SIGN_IN_FRAGMENT_TAG
import net.noliaware.yumi_contributor.commun.FragmentTags.PRIVACY_POLICY_FRAGMENT_TAG
import net.noliaware.yumi_contributor.commun.util.ViewModelState.DataState
import net.noliaware.yumi_contributor.commun.util.ViewModelState.LoadingState
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.commun.util.withArgs
import net.noliaware.yumi_contributor.feature_account.presentation.controllers.PrivacyPolicyFragment
import net.noliaware.yumi_contributor.feature_login.domain.model.AccountData
import net.noliaware.yumi_contributor.feature_login.domain.model.TFAMode
import net.noliaware.yumi_contributor.feature_profile.domain.model.UserProfile
import net.noliaware.yumi_contributor.feature_profile.presentation.views.ProfileParentView
import net.noliaware.yumi_contributor.feature_profile.presentation.views.ProfileView.ProfileViewAdapter
import net.noliaware.yumi_contributor.feature_profile.presentation.views.ProfileView.ProfileViewCallback

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

    companion object {
        fun newInstance(
            accountData: AccountData?
        ) = UserProfileFragment().withArgs(ACCOUNT_DATA to accountData)
    }

    private var profileParentView: ProfileParentView? = null
    private val viewModel by viewModels<UserProfileFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return container?.inflate(R.layout.profile_layout)?.apply {
            profileParentView = this as ProfileParentView
            profileParentView?.getProfileView?.callback = profileViewCallback
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFlows()
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userProfileEventsHelper.eventFlow.collectLatest { sharedEvent ->
                profileParentView?.activateLoading(false)
                handleSharedEvent(sharedEvent)
                redirectToLoginScreenFromSharedEvent(sharedEvent)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userProfileEventsHelper.stateFlow.collect { vmState ->
                when (vmState) {
                    is LoadingState -> profileParentView?.activateLoading(true)
                    is DataState -> vmState.data?.let { userProfile ->
                        profileParentView?.activateLoading(false)
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
            address = address,
            twoFactorAuthModeText = map2FAModeText(viewModel.accountData?.twoFactorAuthMode),
            twoFactorAuthModeActivated = map2FAModeActivation(viewModel.accountData?.twoFactorAuthMode)
        ).also {
            profileParentView?.getProfileView?.fillViewWithData(it)
        }
    }

    private fun map2FAModeText(
        twoFactorAuthMode: TFAMode?
    ) = when (twoFactorAuthMode) {
        TFAMode.APP -> getString(R.string.bo_two_factor_auth_by_app)
        TFAMode.MAIL -> getString(R.string.bo_two_factor_auth_by_mail)
        else -> getString(R.string.bo_two_factor_auth_none)
    }

    private fun map2FAModeActivation(
        twoFactorAuthMode: TFAMode?
    ) = when (twoFactorAuthMode) {
        TFAMode.APP -> true
        else -> false
    }

    private val profileViewCallback: ProfileViewCallback by lazy {
        object : ProfileViewCallback {
            override fun onGetCodeButtonClicked() {
                BOSignInFragment.newInstance().show(
                    childFragmentManager.beginTransaction(),
                    BO_SIGN_IN_FRAGMENT_TAG
                )
            }

            override fun onPrivacyPolicyButtonClicked() {
                PrivacyPolicyFragment.newInstance(
                    privacyPolicyUrl = viewModel.accountData?.privacyPolicyUrl.orEmpty(),
                    isConfirmationRequired = false
                ).show(
                    childFragmentManager.beginTransaction(),
                    PRIVACY_POLICY_FRAGMENT_TAG
                )
            }
        }
    }

    override fun onDestroyView() {
        profileParentView = null
        super.onDestroyView()
    }
}