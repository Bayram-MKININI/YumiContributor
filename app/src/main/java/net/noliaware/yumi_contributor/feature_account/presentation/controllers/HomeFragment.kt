package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.OnBackPressedHandler
import net.noliaware.yumi_contributor.commun.util.safeNavigate
import net.noliaware.yumi_contributor.feature_account.presentation.views.HomeMenuView.HomeMenuViewCallback
import net.noliaware.yumi_contributor.feature_account.presentation.views.HomeView
import net.noliaware.yumi_contributor.feature_login.domain.model.AccountData
import net.noliaware.yumi_contributor.feature_message.presentation.controllers.MessagingFragmentArgs
import net.noliaware.yumi_contributor.feature_profile.presentation.controllers.UserProfileFragmentArgs
import java.time.Duration

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var homeView: HomeView? = null
    private val args by navArgs<HomeFragmentArgs>()
    private val viewModel by activityViewModels<HomeFragmentViewModel>()
    private val navHostFragment by lazy {
        childFragmentManager.findFragmentById(
            R.id.home_nav_host_fragment
        ) as NavHostFragment
    }
    private val homeNavController by lazy {
        navHostFragment.findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.home_layout,
        container,
        false
    )?.apply {
        homeView = this as HomeView
        homeView?.homeMenuView?.callback = homeMenuViewCallback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBackButtonIntercept()
        homeView?.selectHomeButton()
        homeNavController.setGraph(
            R.navigation.home_nav_graph,
            ManagedAccountsFragmentArgs(args.accountData).toBundle()
        )
        setUpBadges(args.accountData)
        showPrivacyPolicyDialogIfAny(args.accountData)
    }

    private fun setUpBadges(accountData: AccountData) {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(Duration.ofMillis(1000))
            homeView?.homeMenuView?.let { homeMenuView ->
                if (accountData.newMessageCount > 0) {
                    homeMenuView.setBadgeForMailButton(accountData.newMessageCount)
                }
                if (accountData.newAlertCount > 0) {
                    homeMenuView.setBadgeForNotificationButton(accountData.newAlertCount)
                }
            }
        }
    }

    private fun showPrivacyPolicyDialogIfAny(accountData: AccountData) {
        if (accountData.shouldConfirmPrivacyPolicy) {
            viewLifecycleOwner.lifecycleScope.launch {
                delay(Duration.ofMillis(150))
                findNavController().safeNavigate(
                    HomeFragmentDirections.actionHomeFragmentToPrivacyPolicyFragment(
                        privacyPolicyUrl = accountData.privacyPolicyUrl,
                        isPrivacyPolicyConfirmationRequired = true
                    )
                )
            }
        }
    }

    private val homeMenuViewCallback: HomeMenuViewCallback by lazy {
        object : HomeMenuViewCallback {
            override fun onHomeButtonClicked() {
                homeView?.selectHomeButton()
                homeNavController.navigate(
                    R.id.action_go_to_ManagedAccount,
                    ManagedAccountsFragmentArgs(
                        args.accountData,
                        viewModel.managedAccount
                    ).toBundle()
                )
            }

            override fun onProfileButtonClicked() {
                homeNavController.navigate(
                    R.id.action_go_to_UserProfileFragment,
                    UserProfileFragmentArgs(args.accountData).toBundle()
                )
            }

            override fun onMailButtonClicked() {
                homeView?.homeMenuView?.hideMailButtonBadge()
                homeNavController.navigate(
                    R.id.action_go_to_MessagingFragment,
                    MessagingFragmentArgs(args.accountData.domainName.orEmpty()).toBundle()
                )
            }

            override fun onNotificationButtonClicked() {
                homeView?.homeMenuView?.hideNotificationButtonBadge()
                homeNavController.navigate(R.id.action_go_to_AlertsFragment)
            }
        }
    }

    private fun setUpBackButtonIntercept() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val onBackPressedHandler = navHostFragment.childFragmentManager.primaryNavigationFragment as? OnBackPressedHandler
                    when {
                        onBackPressedHandler?.onBackPressedHandled() == true -> Unit
                        homeNavController.graph.startDestinationId != homeNavController.currentDestination?.id -> homeView?.performClickOnHomeButton()
                        else -> activity?.finish()
                    }
                }
            })
    }

    override fun onDestroyView() {
        homeView?.homeMenuView?.callback = null
        homeView = null
        super.onDestroyView()
    }
}