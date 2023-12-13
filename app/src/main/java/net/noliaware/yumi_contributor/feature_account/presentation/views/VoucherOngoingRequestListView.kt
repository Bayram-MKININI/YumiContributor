package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.text.SpannableString
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.adapters.BaseAdapter
import net.noliaware.yumi_contributor.commun.util.MarginItemDecoration
import net.noliaware.yumi_contributor.commun.util.activateShimmer
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.drawableIdByName
import net.noliaware.yumi_contributor.commun.util.getStatusBarHeight
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.feature_account.presentation.adapters.VoucherOngoingRequestsAdapter

class VoucherOngoingRequestListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var backgroundView: View
    private lateinit var headerView: View
    private lateinit var backView: View
    private lateinit var categoryImageView: ImageView
    private lateinit var contentView: View
    private lateinit var titleTextView: TextView
    private lateinit var shimmerView: ShimmerFrameLayout
    private lateinit var shimmerRecyclerView: RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    var callback: VoucherOngoingRequestListViewCallback? = null

    var voucherOngoingRequestAdapter
        get() = recyclerView.adapter as VoucherOngoingRequestsAdapter
        set(adapter) {
            recyclerView.adapter = adapter
        }

    fun interface VoucherOngoingRequestListViewCallback {
        fun onBackButtonClicked()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        backgroundView = findViewById(R.id.background_view)
        headerView = findViewById(R.id.header_view)
        backView = findViewById(R.id.back_view)
        backView.setOnClickListener {
            callback?.onBackButtonClicked()
        }

        categoryImageView = findViewById(R.id.category_image_view)

        contentView = findViewById(R.id.content_layout)
        titleTextView = contentView.findViewById(R.id.title_text_view)
        shimmerView = contentView.findViewById(R.id.shimmer_view)
        shimmerRecyclerView = shimmerView.findViewById(R.id.shimmer_recycler_view)
        shimmerRecyclerView.also {
            it.setUp()
            BaseAdapter<Unit>().apply {
                expressionOnCreateViewHolder = { viewGroup ->
                    viewGroup.inflate(R.layout.voucher_ongoing_request_item_placeholder_layout)
                }
                it.adapter = this
                submitList(listOf(Unit))
            }
        }
        recyclerView = contentView.findViewById(R.id.recycler_view)
        recyclerView.setUp()
        emptyView = findViewById(R.id.empty_message_text_view)
    }

    private fun RecyclerView.setUp() {
        layoutManager = LinearLayoutManager(context)
        addItemDecoration(MarginItemDecoration(convertDpToPx(20)))
    }

    fun setUpViewLook(
        color: Int,
        iconName: String?,
        title: SpannableString
    ) {
        headerView.setBackgroundColor(color)
        categoryImageView.setImageResource(context.drawableIdByName(iconName))
        titleTextView.text = title
    }

    fun setLoadingVisible(visible: Boolean) {
        shimmerView.activateShimmer(visible)
        if (visible) {
            shimmerView.isVisible = true
        } else {
            shimmerView.isGone = true
        }
    }

    fun stopLoading() {
        if (shimmerView.isVisible) {
            shimmerView.activateShimmer(false)
        }
    }

    fun setEmptyMessageVisible(visible: Boolean) {
        if (visible) {
            emptyView.isVisible = true
            recyclerView.isGone = true
        } else {
            emptyView.isGone = true
            recyclerView.isVisible = true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        backgroundView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight - getStatusBarHeight(), MeasureSpec.EXACTLY)
        )

        headerView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(
                getStatusBarHeight() + convertDpToPx(92),
                MeasureSpec.EXACTLY
            )
        )

        backView.measureWrapContent()

        categoryImageView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(86), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(86), MeasureSpec.EXACTLY)
        )

        val parentContentViewHeight = viewHeight -
                (headerView.measuredHeight + categoryImageView.measuredHeight / 2 + convertDpToPx(25))

        contentView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 95 / 100, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(parentContentViewHeight, MeasureSpec.EXACTLY)
        )

        titleTextView.measure(
            MeasureSpec.makeMeasureSpec(contentView.measuredWidth * 9 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val recyclerViewHeight =
            contentView.measuredHeight - (titleTextView.measuredHeight + convertDpToPx(25))

        if (shimmerView.isVisible) {
            shimmerView.measure(
                MeasureSpec.makeMeasureSpec(contentView.measuredWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(recyclerViewHeight, MeasureSpec.EXACTLY)
            )
        }

        if (recyclerView.isVisible) {
            recyclerView.measure(
                MeasureSpec.makeMeasureSpec(contentView.measuredWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(recyclerViewHeight, MeasureSpec.EXACTLY)
            )
        }

        if (emptyView.isVisible) {
            emptyView.measureWrapContent()
        }

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        val viewWidth = right - left
        val viewHeight = bottom - top

        backgroundView.layoutToTopLeft(0, getStatusBarHeight())

        headerView.layoutToTopLeft(0, 0)

        backView.layoutToTopLeft(convertDpToPx(10), getStatusBarHeight() + convertDpToPx(10))

        categoryImageView.layoutToTopLeft(
            (viewWidth - categoryImageView.measuredWidth) / 2,
            headerView.bottom - categoryImageView.measuredHeight / 2
        )

        contentView.layoutToTopLeft(
            (viewWidth - contentView.measuredWidth) / 2,
            categoryImageView.bottom + convertDpToPx(15)
        )

        titleTextView.layoutToTopLeft(
            (viewWidth - titleTextView.measuredWidth) / 2,
            convertDpToPx(20)
        )

        if (shimmerView.isVisible) {
            shimmerView.layoutToTopLeft(
                0,
                titleTextView.bottom + convertDpToPx(5)
            )
        }

        if (recyclerView.isVisible) {
            recyclerView.layoutToTopLeft(
                0,
                titleTextView.bottom + convertDpToPx(5)
            )
        }

        if (emptyView.isVisible) {
            emptyView.layoutToTopLeft(
                (contentView.measuredWidth - emptyView.measuredWidth) / 2,
                (contentView.measuredHeight - emptyView.measuredHeight) / 2
            )
        }
    }
}