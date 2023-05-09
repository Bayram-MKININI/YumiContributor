package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.ACCOUNT_DATA
import net.noliaware.yumi_contributor.commun.MANAGED_ACCOUNT
import net.noliaware.yumi_contributor.commun.util.formatNumber
import net.noliaware.yumi_contributor.commun.util.getSerializableCompat
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.withArgs
import net.noliaware.yumi_contributor.feature_account.domain.model.ManagedAccount
import net.noliaware.yumi_contributor.feature_account.domain.model.SelectableData
import net.noliaware.yumi_contributor.feature_account.presentation.views.ManagedAccountParentView
import net.noliaware.yumi_contributor.feature_login.domain.model.AccountData

@AndroidEntryPoint
class ManagedAccountFragment : Fragment() {

    companion object {
        fun newInstance(
            accountData: AccountData?,
            managedAccount: ManagedAccount?
        ) = ManagedAccountFragment().withArgs(
            ACCOUNT_DATA to accountData,
            MANAGED_ACCOUNT to managedAccount
        )
    }

    private var managedAccountParentView: ManagedAccountParentView? = null
    private val viewModel: ManagedAccountFragmentViewModel by activityViewModels()
    var onManagedAccountSelected: ((ManagedAccount) -> Unit)? = null
    var onBackButtonPressed: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return container?.inflate(R.layout.managed_account_layout)?.apply {
            managedAccountParentView = this as ManagedAccountParentView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addDefaultManagedAccountIfAny()
        setUpWelcomeMessage()
        setUpViewPager()
        collectFlow()
        setUpBackButtonIntercept()
    }

    private fun addDefaultManagedAccountIfAny() {
        arguments?.getSerializableCompat(MANAGED_ACCOUNT, ManagedAccount::class.java)?.let {
            viewModel.setInitManagedAccount(it)
        }
    }

    private fun setUpWelcomeMessage() {
        viewModel.accountData?.let {
            managedAccountParentView?.setUserData(
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
        val viewPager = managedAccountParentView?.getViewPager
        ManagedAccountFragmentStateAdapter(childFragmentManager, lifecycle).apply {
            viewPager?.adapter = this
        }
    }

    private fun collectFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.onBackEventFlow.flowWithLifecycle(lifecycle).collectLatest {
                viewModel.resetSelectedManagedAccount()
                managedAccountParentView?.displayAccountListView()
                onBackButtonPressed?.invoke()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.managedAccountFlow.flowWithLifecycle(lifecycle).collect { managedAccount ->
                when (managedAccount) {
                    is SelectableData.AssignedData -> {
                        managedAccount.data?.let {
                            managedAccountParentView?.displaySelectedAccountView(animated = false)
                        }
                    }
                    is SelectableData.SelectedData -> {
                        managedAccount.data?.let {
                            onManagedAccountSelected?.invoke(it)
                            managedAccountParentView?.displaySelectedAccountView()
                        }
                    }
                }
            }
        }
    }

    private fun setUpBackButtonIntercept() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (managedAccountParentView?.getViewPager?.currentItem == 1) {
                        viewModel.sendBackButtonClickedEvent()
                    } else {
                        activity?.finish()
                    }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        managedAccountParentView = null
    }

    private class ManagedAccountFragmentStateAdapter(
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