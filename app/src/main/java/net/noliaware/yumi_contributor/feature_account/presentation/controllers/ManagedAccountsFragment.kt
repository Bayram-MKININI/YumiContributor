package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.OnBackPressedHandler
import net.noliaware.yumi_contributor.commun.util.collectLifecycleAware
import net.noliaware.yumi_contributor.commun.util.formatNumber
import net.noliaware.yumi_contributor.feature_account.domain.model.SelectableData.AssignedData
import net.noliaware.yumi_contributor.feature_account.domain.model.SelectableData.SelectedData
import net.noliaware.yumi_contributor.feature_account.presentation.views.ManagedAccountsParentView

@AndroidEntryPoint
class ManagedAccountsFragment : Fragment(), OnBackPressedHandler {

    private var managedAccountsParentView: ManagedAccountsParentView? = null
    private val args by navArgs<ManagedAccountsFragmentArgs>()
    private val managedAccountsViewModel by viewModels<ManagedAccountsFragmentViewModel>()
    private val homeViewModel by activityViewModels<HomeFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.managed_accounts_layout, container, false)?.apply {
            managedAccountsParentView = this as ManagedAccountsParentView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addDefaultManagedAccountIfAny()
        setUpWelcomeMessage()
        setUpViewPager()
        collectFlow()
    }

    private fun addDefaultManagedAccountIfAny() {
        args.managedAccount?.let {
            managedAccountsViewModel.setInitManagedAccount(it)
        }
    }

    private fun setUpWelcomeMessage() {
        args.accountData.let {
            managedAccountsParentView?.setUserData(
                helloText = it.helloMessage,
                userName = it.userName,
                accountBadgeValue = it.accountCount.formatNumber(),
                accounts = resources.getQuantityString(
                    R.plurals.accounts,
                    it.accountCount
                )
            )
        }
    }

    private fun setUpViewPager() {
        ManagedAccountsFragmentStateAdapter(childFragmentManager, viewLifecycleOwner.lifecycle).apply {
            managedAccountsParentView?.getViewPager?.adapter = this
        }
    }

    private fun collectFlow() {
        managedAccountsViewModel.onBackEventFlow.collectLifecycleAware(viewLifecycleOwner) {
            managedAccountsViewModel.resetSelectedManagedAccount()
            managedAccountsParentView?.displayAccountListView()
            homeViewModel.managedAccount = null
        }
        managedAccountsViewModel.managedAccountFlow.collectLifecycleAware(viewLifecycleOwner) { managedAccount ->
            when (managedAccount) {
                is AssignedData -> {
                    managedAccount.data?.let {
                        managedAccountsParentView?.displaySelectedAccountView(animated = false)
                    }
                }
                is SelectedData -> {
                    managedAccount.data?.let {
                        homeViewModel.managedAccount = it
                        managedAccountsParentView?.displaySelectedAccountView()
                    }
                }
            }
        }
    }

    override fun onBackPressedHandled() =
        if (managedAccountsParentView?.getViewPager?.currentItem == 1) {
            managedAccountsViewModel.sendBackButtonClickedEvent()
            true
        } else {
            false
        }

    override fun onDestroyView() {
        managedAccountsParentView = null
        super.onDestroyView()
    }

    private class ManagedAccountsFragmentStateAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount() = 2
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AccountsListFragment()
                else -> SelectedAccountFragment()
            }
        }
    }
}