package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.adapters.ListLoadStateAdapter
import net.noliaware.yumi_contributor.commun.util.ViewModelState
import net.noliaware.yumi_contributor.commun.util.handlePaginationError
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.makeCall
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.feature_account.domain.model.ManagedAccount
import net.noliaware.yumi_contributor.feature_account.presentation.adapters.FilteredManagedAccountsAdapter
import net.noliaware.yumi_contributor.feature_account.presentation.adapters.PaginatedManagedAccountsAdapter
import net.noliaware.yumi_contributor.feature_account.presentation.mappers.ManagedAccountMapper
import net.noliaware.yumi_contributor.feature_account.presentation.views.AccountsListView
import net.noliaware.yumi_contributor.feature_account.presentation.views.AccountsListView.AccountsListViewCallback

@AndroidEntryPoint
class AccountsListFragment : Fragment() {

    private var accountsListView: AccountsListView? = null
    private val viewModel: ManagedAccountFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.accounts_list_layout, container, false).apply {
            accountsListView = this as AccountsListView
            accountsListView?.callback = accountsListViewCallback
            accountsListView?.paginatedManagedAccountsAdapter = PaginatedManagedAccountsAdapter(
                accountMapper = ManagedAccountMapper(),
                onItemClicked = onAccountClickedAction,
                onPhoneButtonClicked = onPhoneButtonClickedAction
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.resetFilteredManagedAccount()
        collectFlows()
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            accountsListView?.paginatedManagedAccountsAdapter?.loadStateFlow?.collectLatest { loadState ->
                handlePaginationError(loadState)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getUsersEventsHelper.eventFlow.flowWithLifecycle(lifecycle)
                .collectLatest { sharedEvent ->
                    handleSharedEvent(sharedEvent)
                    redirectToLoginScreenFromSharedEvent(sharedEvent)
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getUsersEventsHelper.stateFlow.flowWithLifecycle(lifecycle)
                .collect { vmState ->
                    when (vmState) {
                        is ViewModelState.LoadingState -> Unit
                        is ViewModelState.DataState -> vmState.data?.let { usersList ->
                            accountsListView?.setUpSearchAutoComplete(
                                usersList.map { managedAccount ->
                                    "${managedAccount.firstName} ${managedAccount.lastName} (${managedAccount.login})"
                                }
                            )
                        }
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getFilteredAccountEventsHelper.stateFlow.flowWithLifecycle(lifecycle)
                .collect { vmState ->
                    when (vmState) {
                        is ViewModelState.LoadingState -> Unit
                        is ViewModelState.DataState -> vmState.data?.let { user ->
                            accountsListView?.filteredManagedAccountsAdapter =
                                FilteredManagedAccountsAdapter(
                                    dataSet = listOf(user),
                                    accountMapper = ManagedAccountMapper(),
                                    onItemClicked = onAccountClickedAction,
                                    onPhoneButtonClicked = onPhoneButtonClickedAction
                                )
                            accountsListView?.displayFilteredAccount()
                        }
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getManagedAccounts().collectLatest {
                accountsListView?.paginatedManagedAccountsAdapter?.withLoadStateFooter(
                    footer = ListLoadStateAdapter()
                )
                accountsListView?.paginatedManagedAccountsAdapter?.submitData(it)
            }
        }
    }

    private val onAccountClickedAction: (ManagedAccount) -> Unit = { managedAccount ->
        viewModel.setSelectedManagedAccount(managedAccount)
    }

    private val onPhoneButtonClickedAction: (ManagedAccount) -> Unit = { managedAccount ->
        context?.makeCall(managedAccount.cellPhoneNumber)
    }

    private val accountsListViewCallback: AccountsListViewCallback by lazy {
        object : AccountsListViewCallback {
            override fun onSuggestionSelected(id: String) {
                viewModel.callManagedAccountForUserId(id)
            }

            override fun onSuggestedAccountSelected() {
                viewModel.getFilteredAccountEventsHelper.stateData?.let {
                    viewModel.setSelectedManagedAccount(it)
                }
            }

            override fun onSuggestionCleared() {
                accountsListView?.displayAllAccount()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        accountsListView = null
    }
}