package net.noliaware.yumi_contributor.feature_account.presentation.controllers

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
import net.noliaware.yumi_contributor.commun.ACCOUNTS_LIST_FRAGMENT_TAG
import net.noliaware.yumi_contributor.commun.MANAGED_ACCOUNT
import net.noliaware.yumi_contributor.commun.USED_VOUCHERS_LIST_FRAGMENT_TAG
import net.noliaware.yumi_contributor.commun.VOUCHERS_LIST_FRAGMENT_TAG
import net.noliaware.yumi_contributor.commun.util.ViewModelState
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.commun.util.withArgs
import net.noliaware.yumi_contributor.feature_account.domain.model.Category
import net.noliaware.yumi_contributor.feature_account.domain.model.ManagedAccount
import net.noliaware.yumi_contributor.feature_account.presentation.views.CategoryView.CategoryViewAdapter
import net.noliaware.yumi_contributor.feature_account.presentation.views.ManagedAccountParentView
import net.noliaware.yumi_contributor.feature_account.presentation.views.ManagedAccountParentView.ManagedAccountParentViewAdapter
import net.noliaware.yumi_contributor.feature_account.presentation.views.ManagedAccountParentView.ManagedAccountParentViewCallback

@AndroidEntryPoint
class ManagedAccountFragment : Fragment() {

    companion object {
        fun newInstance(managedAccount: ManagedAccount?) = ManagedAccountFragment().withArgs(
            MANAGED_ACCOUNT to managedAccount
        )
    }

    private var managedAccountParentView: ManagedAccountParentView? = null
    private val viewModel by viewModels<ManagedAccountFragmentViewModel>()
    var onAccountSelected: ((ManagedAccount) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return container?.inflate(R.layout.managed_account_layout, false)?.apply {
            managedAccountParentView = this as ManagedAccountParentView
            managedAccountParentView?.callback = managedAccountParentViewCallback
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFlows()
        viewModel.managedAccount?.let {
            setupSelectedAccount(it)
        }
    }

    private fun collectFlows() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.selectAccountEventsHelper.eventFlow.collectLatest { sharedEvent ->
                handleSharedEvent(sharedEvent)
                redirectToLoginScreenFromSharedEvent(sharedEvent)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.selectAccountEventsHelper.stateFlow.collect { vmState ->
                when (vmState) {
                    is ViewModelState.LoadingState -> Unit
                    is ViewModelState.DataState -> vmState.data?.let { accountId ->
                        viewModel.callGetCategories()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.categoriesEventsHelper.stateFlow.collect { vmState ->
                when (vmState) {
                    is ViewModelState.LoadingState -> Unit
                    is ViewModelState.DataState -> vmState.data?.let { categoryList ->

                        managedAccountParentView?.refreshAvailableCategoryList(
                            filterAvailableEmptyCategories(
                                categoryList
                            ).map { category ->
                                CategoryViewAdapter(
                                    count = category.availableVoucherCount,
                                    iconName = category.categoryIcon,
                                    title = category.categoryShortLabel
                                )
                            })

                        managedAccountParentView?.refreshUsedCategoryList(
                            filterUsedEmptyCategories(
                                categoryList
                            ).map { category ->
                                CategoryViewAdapter(
                                    count = category.usedVoucherCount,
                                    iconName = category.categoryIcon,
                                    title = category.categoryShortLabel
                                )
                            })
                    }
                }
            }
        }
    }

    private val managedAccountParentViewCallback: ManagedAccountParentViewCallback by lazy {
        object : ManagedAccountParentViewCallback {
            override fun onAvailableItemClickedAtIndex(index: Int) {
                viewModel.categoriesEventsHelper.stateData?.let { categoryList ->
                    filterAvailableEmptyCategories(categoryList)[index]
                        .let { category ->
                            VouchersListFragment.newInstance(
                                category.categoryId,
                                category.categoryLabel
                            ).apply {
                                this.onDataRefreshed = {
                                    viewModel.callGetCategories()
                                }
                            }.show(
                                childFragmentManager.beginTransaction(),
                                VOUCHERS_LIST_FRAGMENT_TAG
                            )
                        }
                }
            }

            override fun onUsedItemClickedAtIndex(index: Int) {
                viewModel.categoriesEventsHelper.stateData?.let { categoryList ->
                    filterUsedEmptyCategories(categoryList)[index]
                        .let { category ->
                            UsedVouchersListFragment.newInstance(
                                category.categoryId,
                                category.categoryLabel
                            ).show(
                                childFragmentManager.beginTransaction(),
                                USED_VOUCHERS_LIST_FRAGMENT_TAG
                            )
                        }
                }
            }

            override fun onChangeAccountClicked() {
                ManagedAccountsListFragment.newInstance()
                    .apply {
                        this.onManagedAccountSelected = { managedAccount ->
                            setupSelectedAccount(managedAccount)
                            onAccountSelected?.invoke(managedAccount)
                        }
                    }.show(
                        childFragmentManager.beginTransaction(),
                        ACCOUNTS_LIST_FRAGMENT_TAG
                    )
            }
        }
    }

    private fun setupSelectedAccount(managedAccount: ManagedAccount) {
        ManagedAccountParentViewAdapter(
            description = "${managedAccount.title} ${managedAccount.firstName} ${managedAccount.lastName}"
        ).apply {
            managedAccountParentView?.fillViewWithData(this)
        }
        viewModel.callSelectAccountForId(managedAccount.login)
    }

    private fun filterAvailableEmptyCategories(categoryList: List<Category>) = categoryList.filter {
        it.availableVoucherCount > 0
    }

    private fun filterUsedEmptyCategories(categoryList: List<Category>) = categoryList.filter {
        it.usedVoucherCount > 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        managedAccountParentView = null
    }
}