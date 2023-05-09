package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.adapters.BaseAdapter
import net.noliaware.yumi_contributor.commun.util.GRID
import net.noliaware.yumi_contributor.commun.util.MarginItemDecoration
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.weak
import net.noliaware.yumi_contributor.feature_account.presentation.views.CategoryItemView.*

class CategoriesView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {

    private val categoryViewAdapters = mutableListOf<CategoryItemViewAdapter>()
    var callback: CategoriesViewCallback? by weak()

    fun interface CategoriesViewCallback {
        fun onCategoryClickedAtIndex(index: Int)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        layoutManager = GridLayoutManager(
            context,
            context.resources.getInteger(R.integer.number_of_columns_for_categories)
        )

        val spacing = convertDpToPx(10)

        setPadding(spacing, spacing, spacing, spacing)
        clipToPadding = false
        clipChildren = false
        addItemDecoration(MarginItemDecoration(spacing, GRID))

        BaseAdapter(categoryViewAdapters).apply {
            expressionViewHolderBinding = { eachItem, view ->
                (view as CategoryItemView).fillViewWithData(eachItem)
            }

            expressionOnCreateViewHolder = { viewGroup ->
                viewGroup.inflate(R.layout.category_item_layout)
            }

            onItemClicked = { position ->
                callback?.onCategoryClickedAtIndex(position)
            }

            adapter = this
        }
    }

    fun fillViewWithData(adapters: List<CategoryItemViewAdapter>) {
        if (categoryViewAdapters.isNotEmpty())
            categoryViewAdapters.clear()
        categoryViewAdapters.addAll(adapters)
        adapter?.notifyDataSetChanged()
    }
}