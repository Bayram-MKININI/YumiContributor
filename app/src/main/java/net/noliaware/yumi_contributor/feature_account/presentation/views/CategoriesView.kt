package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.adapters.BaseAdapter
import net.noliaware.yumi_contributor.commun.util.GRID
import net.noliaware.yumi_contributor.commun.util.MarginItemDecoration
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.weak
import net.noliaware.yumi_contributor.feature_account.presentation.views.CategoryItemView.*

class CategoriesView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var shimmerView: ShimmerFrameLayout
    private lateinit var shimmerRecyclerView: RecyclerView
    private lateinit var recyclerView: RecyclerView
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
        shimmerView = findViewById(R.id.shimmer_view)
        shimmerRecyclerView = shimmerView.findViewById(R.id.shimmer_recycler_view)
        shimmerRecyclerView.also {
            it.setUp()
            BaseAdapter((0..9).map { 0 }).apply {
                expressionOnCreateViewHolder = { viewGroup ->
                    viewGroup.inflate(R.layout.category_item_placeholder_layout)
                }
                it.adapter = this
            }
        }
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.also {
            it.setUp()
            it.setHasFixedSize(true)
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
                it.adapter = this
            }
        }
    }

    private fun RecyclerView.setUp() {
        layoutManager = GridLayoutManager(
            context,
            resources.getInteger(R.integer.number_of_columns_for_categories)
        )

        val spacing = convertDpToPx(10)
        setPadding(spacing, spacing, spacing, spacing)
        clipToPadding = false
        clipChildren = false
        addItemDecoration(MarginItemDecoration(spacing, GRID))
    }

    fun fillViewWithData(adapters: List<CategoryItemViewAdapter>) {
        if (categoryViewAdapters.isNotEmpty())
            categoryViewAdapters.clear()
        categoryViewAdapters.addAll(adapters)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    fun setLoadingVisible(visible: Boolean) {
        if (visible) {
            shimmerView.isVisible = true
            recyclerView.isGone = true
            shimmerView.startShimmer()
        } else {
            shimmerView.isGone = true
            recyclerView.isVisible = true
            shimmerView.stopShimmer()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        if (recyclerView.isVisible) {
            recyclerView.measure(
                MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
            )
        }

        if (shimmerView.isVisible) {
            shimmerView.measure(
                MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
        }

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        if (recyclerView.isVisible) {
            recyclerView.layoutToTopLeft(0, 0)
        }

        if (shimmerView.isVisible) {
            shimmerView.layoutToTopLeft(0, 0)
        }
    }
}