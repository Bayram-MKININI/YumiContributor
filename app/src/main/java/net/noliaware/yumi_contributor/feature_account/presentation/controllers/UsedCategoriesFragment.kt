package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.FragmentTags.USED_VOUCHERS_LIST_FRAGMENT_TAG
import net.noliaware.yumi_contributor.commun.util.ViewModelState
import net.noliaware.yumi_contributor.commun.util.formatNumber
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.feature_account.domain.model.Category
import net.noliaware.yumi_contributor.feature_account.presentation.views.CategoriesView
import net.noliaware.yumi_contributor.feature_account.presentation.views.CategoriesView.CategoriesViewCallback
import net.noliaware.yumi_contributor.feature_account.presentation.views.CategoryItemView.CategoryItemViewAdapter

@AndroidEntryPoint
class UsedCategoriesFragment : Fragment() {

    private var categoriesView: CategoriesView? = null
    private val viewModel by activityViewModels<CategoriesFragmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.categories_layout, container, false).apply {
            categoriesView = this as CategoriesView
            categoriesView?.callback = categoriesViewCallback
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectFlows()
        viewModel.callGetUsedCategories()
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.onDataRefreshedEventFlow.collectLatest {
                viewModel.callGetUsedCategories()
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.usedCategoriesEventsHelper.eventFlow.collectLatest { sharedEvent ->
                handleSharedEvent(sharedEvent)
                redirectToLoginScreenFromSharedEvent(sharedEvent)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.usedCategoriesEventsHelper.stateFlow.collect { vmState ->
                when (vmState) {
                    is ViewModelState.LoadingState -> categoriesView?.setLoadingVisible(true)
                    is ViewModelState.DataState -> vmState.data?.let { categories ->
                        categoriesView?.setLoadingVisible(false)
                        bindViewToData(categories)
                    }
                }
            }
        }
    }

    private fun bindViewToData(categories: List<Category>) {
        val categoryItemViewAdapters = mutableListOf<CategoryItemViewAdapter>()
        categories.map { category ->
            CategoryItemViewAdapter(
                count = category.usedVoucherCount.formatNumber(),
                iconName = category.categoryIcon.orEmpty(),
                title = category.categoryShortLabel
            ).also {
                categoryItemViewAdapters.add(it)
            }
        }
        categoriesView?.fillViewWithData(categoryItemViewAdapters)
    }

    private val categoriesViewCallback: CategoriesViewCallback by lazy {
        CategoriesViewCallback { index ->
            viewModel.usedCategoriesEventsHelper.stateData?.let { categories ->
                categories[index].apply {
                    UsedVouchersListFragment.newInstance(
                        this
                    ).show(
                        childFragmentManager.beginTransaction(),
                        USED_VOUCHERS_LIST_FRAGMENT_TAG
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        categoriesView = null
        super.onDestroyView()
    }
}