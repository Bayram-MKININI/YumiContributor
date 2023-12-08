package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import dagger.hilt.android.AndroidEntryPoint
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.FragmentKeys.REFRESH_VOUCHER_CATEGORY_LIST_REQUEST_KEY
import net.noliaware.yumi_contributor.commun.util.ViewState.DataState
import net.noliaware.yumi_contributor.commun.util.ViewState.LoadingState
import net.noliaware.yumi_contributor.commun.util.collectLifecycleAware
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.feature_account.domain.model.SelectableData.AssignedData
import net.noliaware.yumi_contributor.feature_account.presentation.views.SelectedAccountParentView
import net.noliaware.yumi_contributor.feature_account.presentation.views.SelectedAccountParentView.SelectedAccountViewCallback

@AndroidEntryPoint
class SelectedAccountFragment : Fragment() {

    private var selectedAccountParentView: SelectedAccountParentView? = null
    private val selectedAccountViewModel by viewModels<SelectedAccountFragmentViewModel>()
    private val managedAccountsViewModel by viewModels<ManagedAccountsFragmentViewModel>(
        ownerProducer = {
            requireParentFragment()
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.selected_account_layout,
        container,
        false
    ).apply {
        selectedAccountParentView = this as SelectedAccountParentView
        selectedAccountParentView?.callback = selectedAccountViewCallback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpFragmentListener()
        selectedAccountViewModel.accountData = managedAccountsViewModel.accountData
        collectFlows()
    }

    private fun setUpFragmentListener() {
        setFragmentResultListener(
            REFRESH_VOUCHER_CATEGORY_LIST_REQUEST_KEY
        ) { _, _ ->
            selectedAccountViewModel.sendCategoriesListsRefreshedEvent()
        }
    }

    private fun collectFlows() {
        managedAccountsViewModel.managedAccountFlow.collectLifecycleAware(viewLifecycleOwner) { managedAccount ->
            when (managedAccount) {
                is AssignedData -> {
                    if (selectedAccountParentView?.getViewPager?.adapter == null) {
                        setUpView()
                    }
                }
                else -> Unit
            }
        }
        managedAccountsViewModel.selectAccountEventsHelper.eventFlow.collectLifecycleAware(
            viewLifecycleOwner
        ) { sharedEvent ->
            selectedAccountParentView?.activateLoading(false)
            managedAccountsViewModel.selectAccountEventsHelper.resetStateData()
            handleSharedEvent(sharedEvent)
            redirectToLoginScreenFromSharedEvent(sharedEvent)
        }
        managedAccountsViewModel.selectAccountEventsHelper.stateFlow.collectLifecycleAware(
            viewLifecycleOwner
        ) { viewState ->
            when (viewState) {
                is LoadingState -> selectedAccountParentView?.activateLoading(true)
                is DataState -> viewState.data?.let {
                    selectedAccountParentView?.activateLoading(false)
                    managedAccountsViewModel.selectAccountEventsHelper.resetStateData()
                    setUpView()
                }
            }
        }
    }

    private fun setUpView() {
        managedAccountsViewModel.getSelectedManagedAccount()?.let { managedAccount ->
            selectedAccountParentView?.setTitle(
                "${managedAccount.title} ${managedAccount.firstName} ${managedAccount.lastName}"
            )
        }
        SelectedAccountFragmentStateAdapter(
            childFragmentManager,
            viewLifecycleOwner.lifecycle
        ).apply {
            selectedAccountParentView?.getViewPager?.adapter = this
        }
    }

    private val selectedAccountViewCallback: SelectedAccountViewCallback by lazy {
        SelectedAccountViewCallback {
            managedAccountsViewModel.sendBackButtonClickedEvent()
        }
    }

    override fun onDestroyView() {
        selectedAccountParentView = null
        super.onDestroyView()
    }

    private class SelectedAccountFragmentStateAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount() = 3
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AvailableCategoriesListFragment()
                1 -> UsedCategoriesListFragment()
                else -> CancelledCategoriesListFragment()
            }
        }
    }
}