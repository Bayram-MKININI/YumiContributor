package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.text.SpannableString
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.*
import net.noliaware.yumi_contributor.feature_account.presentation.adapters.VoucherAdapter

class VouchersListView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var headerView: View
    private lateinit var backView: View
    private lateinit var backgroundView: View
    private lateinit var categoryImageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var recyclerView: RecyclerView
    var voucherAdapter
        get() = recyclerView.adapter as VoucherAdapter
        set(adapter) {
            recyclerView.adapter = adapter
        }
    var callback: VouchersListViewCallback? by weak()

    fun interface VouchersListViewCallback {
        fun onBackButtonClicked()
    }

    data class VouchersListViewAdapter(
        val title: SpannableString,
        val color: Int,
        val iconName: String?
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        headerView = findViewById(R.id.header_view)
        backView = findViewById(R.id.back_view)
        backView.setOnClickListener {
            callback?.onBackButtonClicked()
        }

        backgroundView = findViewById(R.id.background_view)
        categoryImageView = findViewById(R.id.category_image_view)
        titleTextView = findViewById(R.id.title_text_view)
        recyclerView = findViewById(R.id.recycler_view)

        recyclerView.also {
            it.layoutManager = LinearLayoutManager(context)
            it.addItemDecoration(MarginItemDecoration(convertDpToPx(20)))
        }
    }

    fun fillViewWithData(vouchersListViewAdapter: VouchersListViewAdapter) {
        titleTextView.text = vouchersListViewAdapter.title
        headerView.setBackgroundColor(vouchersListViewAdapter.color)
        categoryImageView.setImageResource(context.drawableIdByName(vouchersListViewAdapter.iconName))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        headerView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(
                getStatusBarHeight() + convertDpToPx(92),
                MeasureSpec.EXACTLY
            )
        )

        backView.measureWrapContent()

        backgroundView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight - getStatusBarHeight(), MeasureSpec.EXACTLY)
        )

        categoryImageView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(86), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(86), MeasureSpec.EXACTLY)
        )

        titleTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 9 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val recyclerViewHeight = viewHeight - (headerView.measuredHeight + categoryImageView.measuredHeight / 2 +
                titleTextView.measuredHeight + convertDpToPx(35))

        recyclerView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(recyclerViewHeight, MeasureSpec.EXACTLY)
        )

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

        titleTextView.layoutToTopLeft(
            (viewWidth - titleTextView.measuredWidth) / 2,
            categoryImageView.bottom + convertDpToPx(20)
        )

        recyclerView.layoutToTopLeft(0, titleTextView.bottom + convertDpToPx(15))
    }
}