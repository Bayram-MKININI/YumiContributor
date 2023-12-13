package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.ViewState.DataState
import net.noliaware.yumi_contributor.commun.util.ViewState.LoadingState
import net.noliaware.yumi_contributor.commun.util.collectLifecycleAware
import net.noliaware.yumi_contributor.commun.util.formatNumber
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.commun.util.safeNavigate
import net.noliaware.yumi_contributor.feature_account.domain.model.Category
import net.noliaware.yumi_contributor.feature_account.presentation.views.CategoriesListView
import net.noliaware.yumi_contributor.feature_account.presentation.views.CategoriesListView.CategoriesListViewCallback
import net.noliaware.yumi_contributor.feature_account.presentation.views.CategoryItemView.CategoryItemViewAdapter

@AndroidEntryPoint
class AvailableCategoriesListFragment : Fragment() {

    private var categoriesListView: CategoriesListView? = null
    private val viewModel by viewModels<SelectedAccountFragmentViewModel>(
        ownerProducer = {
            requireParentFragment()
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.categories_list_layout,
        container,
        false
    ).apply {
        categoriesListView = this as CategoriesListView
        categoriesListView?.callback = categoriesListViewCallback
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFlows()
        categoriesListView?.setLoadingVisible(true)
        viewModel.callGetAvailableCategories()
    }

    private fun collectFlows() {
        viewModel.onAvailableCategoriesListRefreshedEventFlow.collectLifecycleAware(viewLifecycleOwner) {
            viewModel.callGetAvailableCategories()
        }
        viewModel.availableCategoriesEventsHelper.eventFlow.collectLifecycleAware(viewLifecycleOwner) { sharedEvent ->
            categoriesListView?.stopLoading()
            handleSharedEvent(sharedEvent)
            redirectToLoginScreenFromSharedEvent(sharedEvent)
        }
        viewModel.availableCategoriesEventsHelper.stateFlow.collectLifecycleAware(viewLifecycleOwner) { viewState ->
            when (viewState) {
                is LoadingState -> Unit
                is DataState -> viewState.data?.let { categories ->
                    categoriesListView?.setLoadingVisible(false)
                    bindViewToData(categories)
                }
            }
        }
    }

    private fun bindViewToData(categories: List<Category>) {
        val categoryItemViewAdapters = mutableListOf<CategoryItemViewAdapter>()
        categories.map { category ->
            CategoryItemViewAdapter(
                count = category.availableVoucherCount.formatNumber(),
                iconName = category.categoryIcon.orEmpty(),
                title = category.categoryShortLabel
            ).also {
                categoryItemViewAdapters.add(it)
            }
        }
        categoriesListView?.fillViewWithData(categoryItemViewAdapters)
    }

    private val categoriesListViewCallback: CategoriesListViewCallback by lazy {
        CategoriesListViewCallback { index ->
            viewModel.availableCategoriesEventsHelper.stateData?.let { categories ->
                categories[index].apply {
                    findNavController().safeNavigate(
                        ManagedAccountsFragmentDirections.actionCategoriesFragmentToAvailableVouchersListFragment(
                            this,
                            viewModel.accountData?.voucherRequestTypes?.toTypedArray() ?: arrayOf()
                        )
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        categoriesListView?.callback = null
        categoriesListView = null
        super.onDestroyView()
    }
}