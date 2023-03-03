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
import net.noliaware.yumi_contributor.commun.USED_VOUCHERS_LIST_FRAGMENT_TAG
import net.noliaware.yumi_contributor.commun.util.ViewModelState
import net.noliaware.yumi_contributor.commun.util.formatNumber
import net.noliaware.yumi_contributor.commun.util.handleSharedEvent
import net.noliaware.yumi_contributor.commun.util.redirectToLoginScreenFromSharedEvent
import net.noliaware.yumi_contributor.feature_account.domain.model.Category
import net.noliaware.yumi_contributor.feature_account.presentation.views.CategoriesView
import net.noliaware.yumi_contributor.feature_account.presentation.views.CategoryItemView

@AndroidEntryPoint
class UsedCategoriesFragment : Fragment() {

    private var categoriesView: CategoriesView? = null
    private val viewModel by viewModels<UsedCategoriesFragmentViewModel>()

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
    }

    private fun collectFlows() {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsHelper.eventFlow.collectLatest { sharedEvent ->
                handleSharedEvent(sharedEvent)
                redirectToLoginScreenFromSharedEvent(sharedEvent)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventsHelper.stateFlow.collect { vmState ->
                when (vmState) {
                    is ViewModelState.LoadingState -> Unit
                    is ViewModelState.DataState -> vmState.data?.let { usedCategories ->
                        bindViewToData(usedCategories)
                    }
                }
            }
        }
    }

    private fun bindViewToData(usedCategories: List<Category>) {
        val categoryItemViewAdapters = mutableListOf<CategoryItemView.CategoryItemViewAdapter>()
        usedCategories.map { category ->
            CategoryItemView.CategoryItemViewAdapter(
                count = category.usedVoucherCount.formatNumber(),
                iconName = category.categoryIcon.orEmpty(),
                title = category.categoryShortLabel
            ).also {
                categoryItemViewAdapters.add(it)
            }
        }
        categoriesView?.fillViewWithData(categoryItemViewAdapters)
    }

    private val categoriesViewCallback: CategoriesView.CategoriesViewCallback by lazy {
        CategoriesView.CategoriesViewCallback { index ->
            viewModel.eventsHelper.stateData?.let { categories ->
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
        super.onDestroyView()
        categoriesView = null
    }
}