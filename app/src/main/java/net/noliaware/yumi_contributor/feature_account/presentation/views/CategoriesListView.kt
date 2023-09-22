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
import net.noliaware.yumi_contributor.commun.util.activateShimmer
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.weak
import net.noliaware.yumi_contributor.feature_account.presentation.views.CategoryItemView.*

class CategoriesListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var shimmerView: ShimmerFrameLayout
    private lateinit var shimmerRecyclerView: RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: BaseAdapter<CategoryItemViewAdapter>
    var callback: CategoriesListViewCallback? by weak()

    fun interface CategoriesListViewCallback {
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
            BaseAdapter<Int>().apply {
                expressionOnCreateViewHolder = { viewGroup ->
                    viewGroup.inflate(R.layout.category_item_placeholder_layout)
                }
                it.adapter = this
                submitList((0..9).map { 0 })
            }
        }
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.also {
            it.setUp()
            recyclerAdapter = BaseAdapter<CategoryItemViewAdapter>(
                compareItems = { old, new ->
                    old.title == new.title
                },
                compareContents = { old, new ->
                    old == new
                }
            ).apply {
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
        recyclerAdapter.submitList(adapters)
    }

    fun setLoadingVisible(visible: Boolean) {
        shimmerView.activateShimmer(visible)
        if (visible) {
            shimmerView.isVisible = true
            recyclerView.isGone = true
        } else {
            shimmerView.isGone = true
            recyclerView.isVisible = true
        }
    }

    fun stopLoading() {
        if (shimmerView.isVisible) {
            shimmerView.activateShimmer(false)
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
                MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
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