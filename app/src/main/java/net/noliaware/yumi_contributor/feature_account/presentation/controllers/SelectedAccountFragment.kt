package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import net.noliaware.yumi_contributor.commun.util.ViewModelState
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.feature_account.presentation.views.SelectedAccountView
import net.noliaware.yumi_contributor.feature_account.presentation.views.SelectedAccountView.SelectedAccountViewCallback

@AndroidEntryPoint
class SelectedAccountFragment : Fragment() {

    private var selectedAccountView: SelectedAccountView? = null
    private val viewModel: ManagedAccountFragmentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.selected_account_layout, container, false).apply {
            selectedAccountView = this as SelectedAccountView
            selectedAccountView?.callback = selectedAccountViewCallback
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        collectFlows()
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectAccountEventsHelper.eventFlow.flowWithLifecycle(lifecycle)
                .collectLatest { sharedEvent ->
                    handleSharedEvent(sharedEvent)
                    redirectToLoginScreenFromSharedEvent(sharedEvent)
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectAccountEventsHelper.stateFlow.flowWithLifecycle(lifecycle)
                .collect { vmState ->
                    when (vmState) {
                        is ViewModelState.LoadingState -> Unit
                        is ViewModelState.DataState -> vmState.data?.let { accountId ->
                            setUserName()
                            setUpViewPager()
                        }
                    }
                }
        }
    }

    private fun setUserName() {
        viewModel.getSelectedManagedAccount()?.let { managedAccount ->
            selectedAccountView?.setTitle(
                "${managedAccount.title} ${managedAccount.firstName} ${managedAccount.lastName}"
            )
        }
    }

    private fun setUpViewPager() {
        val viewPager = selectedAccountView?.getViewPager
        SelectedAccountFragmentStateAdapter(childFragmentManager, lifecycle).apply {
            viewPager?.adapter = this
        }
    }

    private val selectedAccountViewCallback: SelectedAccountViewCallback by lazy {
        SelectedAccountViewCallback {
            viewModel.sendBackButtonClickedEvent()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        selectedAccountView = null
    }

    private class SelectedAccountFragmentStateAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount() = 3
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AvailableCategoriesFragment()
                1 -> UsedCategoriesFragment()
                else -> CancelledCategoriesFragment()
            }
        }
    }
}