package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.GOLDEN_RATIO
import net.noliaware.yumi_contributor.commun.presentation.adapters.BaseAdapter
import net.noliaware.yumi_contributor.commun.util.GRID
import net.noliaware.yumi_contributor.commun.util.MarginItemDecoration
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.getStatusBarHeight
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.layoutToBottomLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.onItemClicked
import net.noliaware.yumi_contributor.commun.util.weak
import net.noliaware.yumi_contributor.feature_account.presentation.views.CategoryView.CategoryViewAdapter
import kotlin.math.roundToInt

class ManagedAccountParentView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var parentContentView: View
    private lateinit var contentView: ManagedAccountView
    private lateinit var changeAccountTextView: TextView
    private val availableCategoryViewAdapters = mutableListOf<CategoryViewAdapter>()
    private val usedCategoryViewAdapters = mutableListOf<CategoryViewAdapter>()
    var callback: ManagedAccountParentViewCallback? by weak()

    data class ManagedAccountParentViewAdapter(
        val description: String
    )

    interface ManagedAccountParentViewCallback {
        fun onAvailableItemClickedAtIndex(index: Int)
        fun onUsedItemClickedAtIndex(index: Int)
        fun onChangeAccountClicked()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        titleTextView = findViewById(R.id.title_text_view)
        descriptionTextView = findViewById(R.id.description_text_view)
        parentContentView = findViewById(R.id.parent_content_view)
        contentView = findViewById(R.id.managed_account_view)
        changeAccountTextView = findViewById(R.id.change_account_text_view)
        changeAccountTextView.setOnClickListener { callback?.onChangeAccountClicked() }

        setupRecyclerView(
            contentView.availableCategoriesRecyclerView,
            availableCategoryViewAdapters
        ).run {
            this.onItemClicked(onClick = { position, _ ->
                callback?.onAvailableItemClickedAtIndex(position)
            })
        }

        setupRecyclerView(
            contentView.usedCategoriesRecyclerView,
            usedCategoryViewAdapters
        ).run {
            this.onItemClicked(onClick = { position, _ ->
                callback?.onUsedItemClickedAtIndex(position)
            })
        }
    }

    private fun setupRecyclerView(
        recyclerView: RecyclerView,
        adapters: List<CategoryViewAdapter>
    ): RecyclerView {
        val adapter = BaseAdapter(adapters)

        adapter.expressionViewHolderBinding = { eachItem, view ->
            (view as CategoryView).fillViewWithData(eachItem)
        }

        adapter.expressionOnCreateViewHolder = { viewGroup ->
            viewGroup.inflate(R.layout.category_item_layout, false)
        }

        return recyclerView.also {

            it.layoutManager = GridLayoutManager(
                context,
                context.resources.getInteger(R.integer.number_of_columns_for_categories)
            )

            val spacing = convertDpToPx(10)

            it.setPadding(spacing, spacing, spacing, spacing)
            it.clipToPadding = false
            it.clipChildren = false
            it.addItemDecoration(MarginItemDecoration(spacing, GRID))
            it.adapter = adapter
        }
    }

    fun fillViewWithData(viewAdapter: ManagedAccountParentViewAdapter) {
        descriptionTextView.text = viewAdapter.description
    }

    fun refreshAvailableCategoryList(availableCategoryViewAdapters: List<CategoryViewAdapter>) {
        if (availableCategoryViewAdapters.isEmpty()) {
            contentView.isGone = true
        } else {
            contentView.isVisible = true
            refreshCategoryList(
                this.availableCategoryViewAdapters,
                availableCategoryViewAdapters,
                contentView.availableCategoriesRecyclerView
            )
        }
    }

    fun refreshUsedCategoryList(usedCategoryViewAdapters: List<CategoryViewAdapter>) {
        if (usedCategoryViewAdapters.isEmpty()) {
            contentView.usedVouchersTextView.isGone = true
            contentView.usedCategoriesRecyclerView.isGone = true
        } else {
            contentView.usedVouchersTextView.isVisible = true
            contentView.usedCategoriesRecyclerView.isVisible = true
            refreshCategoryList(
                this.usedCategoryViewAdapters,
                usedCategoryViewAdapters,
                contentView.usedCategoriesRecyclerView
            )
        }
    }

    private fun refreshCategoryList(
        oldAdapters: MutableList<CategoryViewAdapter>,
        newAdapters: List<CategoryViewAdapter>?,
        recyclerView: RecyclerView
    ) {
        newAdapters?.let {
            if (oldAdapters.isNotEmpty())
                oldAdapters.clear()

            oldAdapters.addAll(newAdapters)
            recyclerView.adapter?.notifyDataSetChanged()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        titleTextView.measureWrapContent()

        descriptionTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 8 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        changeAccountTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 8 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(40), MeasureSpec.EXACTLY)
        )

        val parentContentViewHeight =
            viewHeight - (titleTextView.measuredHeight + descriptionTextView.measuredHeight + changeAccountTextView.measuredHeight +
                    getStatusBarHeight() + convertDpToPx(80))

        parentContentView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(parentContentViewHeight, MeasureSpec.EXACTLY)
        )

        if (contentView.measuredHeight < parentContentView.measuredHeight)
            contentView.measure(
                MeasureSpec.makeMeasureSpec(contentView.measuredWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(parentContentView.measuredHeight, MeasureSpec.EXACTLY)
            )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        titleTextView.layoutToTopLeft(
            (viewWidth - titleTextView.measuredWidth) / 2,
            getStatusBarHeight() + convertDpToPx(15)
        )
        descriptionTextView.layoutToTopLeft(
            (viewWidth - descriptionTextView.measuredWidth) / 2,
            titleTextView.bottom + convertDpToPx(10)
        )

        parentContentView.layoutToTopLeft(
            0,
            descriptionTextView.bottom + convertDpToPx(10)
        )

        val changeAccountTextViewBottom = if (contentView.isVisible) {
            viewHeight - convertDpToPx(20)
        } else {
            (viewHeight / GOLDEN_RATIO).roundToInt()
        }

        changeAccountTextView.layoutToBottomLeft(
            (viewWidth - changeAccountTextView.measuredWidth) / 2,
            changeAccountTextViewBottom
        )
    }
}