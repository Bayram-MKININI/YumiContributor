package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.ACCOUNT_DATA
import net.noliaware.yumi_contributor.commun.PRIVACY_POLICY_FRAGMENT_TAG
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.withArgs
import net.noliaware.yumi_contributor.feature_account.presentation.views.HomeMenuView
import net.noliaware.yumi_contributor.feature_account.presentation.views.HomeView
import net.noliaware.yumi_contributor.feature_alerts.presentation.controllers.AlertsFragment
import net.noliaware.yumi_contributor.feature_login.domain.model.AccountData
import net.noliaware.yumi_contributor.feature_message.presentation.controllers.MessagingFragment
import net.noliaware.yumi_contributor.feature_profile.presentation.controllers.UserProfileFragment

@AndroidEntryPoint
class HomeFragment : Fragment() {

    companion object {
        fun newInstance(
            accountData: AccountData
        ) = HomeFragment().withArgs(ACCOUNT_DATA to accountData)
    }

    private var homeView: HomeView? = null
    private val viewModel by viewModels<HomeFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return container?.inflate(R.layout.home_layout)?.apply {
            homeView = this as HomeView
            homeView?.homeMenuView?.callback = homeMenuViewCallback
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        displayManagedAccountFragment()
        viewModel.accountData?.let { accountData ->
            homeView?.homeMenuView?.let { homeMenuView ->
                if (accountData.newMessageCount > 0) {
                    homeMenuView.setBadgeForMailButton(accountData.newMessageCount)
                }
                if (accountData.newAlertCount > 0) {
                    homeMenuView.setBadgeForNotificationButton(accountData.newAlertCount)
                }
                if (accountData.shouldConfirmPrivacyPolicy) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        delay(150)
                        PrivacyPolicyFragment.newInstance(
                            privacyPolicyUrl = accountData.privacyPolicyUrl,
                            isConfirmationRequired = true
                        ).show(
                            childFragmentManager.beginTransaction(),
                            PRIVACY_POLICY_FRAGMENT_TAG
                        )
                    }
                }
            }
        }
    }

    private val homeMenuViewCallback: HomeMenuView.HomeMenuViewCallback by lazy {
        object : HomeMenuView.HomeMenuViewCallback {
            override fun onCategoryButtonClicked() {
                displayManagedAccountFragment()
            }

            override fun onProfileButtonClicked() {
                childFragmentManager.beginTransaction().run {
                    replace(
                        R.id.main_fragment_container,
                        UserProfileFragment.newInstance(viewModel.accountData)
                    )
                    commit()
                }
            }

            override fun onMailButtonClicked() {
                homeView?.homeMenuView?.hideMailButtonBadge()
                childFragmentManager.beginTransaction().run {
                    replace(
                        R.id.main_fragment_container,
                        MessagingFragment.newInstance(viewModel.accountData?.messageSubjects)
                    )
                    commit()
                }
            }

            override fun onNotificationButtonClicked() {
                homeView?.homeMenuView?.hideNotificationButtonBadge()
                childFragmentManager.beginTransaction().run {
                    replace(R.id.main_fragment_container, AlertsFragment())
                    commit()
                }
            }
        }
    }

    private fun displayManagedAccountFragment() {
        childFragmentManager.beginTransaction().run {
            replace(
                R.id.main_fragment_container,
                ManagedAccountFragment.newInstance(
                    viewModel.managedAccount
                ).apply {
                    this.onBackButtonPressed = {
                        viewModel.managedAccount = null
                    }
                    this.onManagedAccountSelected = { managedAccount ->
                        viewModel.managedAccount = managedAccount
                    }
                }
            )
            commit()
            homeView?.selectHomeButton()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        homeView = null
    }
}