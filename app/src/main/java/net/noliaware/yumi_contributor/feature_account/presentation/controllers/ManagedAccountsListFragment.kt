package net.noliaware.yumi_contributor.feature_account.presentation.controllers

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
import net.noliaware.yumi_contributor.commun.presentation.adapters.ListLoadStateAdapter
import net.noliaware.yumi_contributor.commun.util.handlePaginationError
import net.noliaware.yumi_contributor.feature_account.domain.model.ManagedAccount
import net.noliaware.yumi_contributor.feature_account.presentation.adapters.ManagedAccountsAdapter
import net.noliaware.yumi_contributor.feature_login.presentation.views.AccountsListView

@AndroidEntryPoint
class ManagedAccountsListFragment : AppCompatDialogFragment() {

    companion object {
        fun newInstance() = ManagedAccountsListFragment()
    }

    private var accountsListView: AccountsListView? = null
    private val viewModel: ManagedAccountsListFragmentViewModel by viewModels()
    var onManagedAccountSelected: ((ManagedAccount) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.accounts_list_layout, container, false).apply {
            accountsListView = this as AccountsListView
            accountsListView?.managedAccountsAdapter = ManagedAccountsAdapter { managedAccount ->
                onManagedAccountSelected?.invoke(managedAccount)
                dismiss()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFlows()
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            accountsListView?.managedAccountsAdapter?.loadStateFlow?.collectLatest { loadState ->
                handlePaginationError(loadState)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getManagedAccounts().collectLatest {
                accountsListView?.managedAccountsAdapter?.withLoadStateFooter(
                    footer = ListLoadStateAdapter()
                )
                accountsListView?.managedAccountsAdapter?.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        accountsListView = null
    }
}