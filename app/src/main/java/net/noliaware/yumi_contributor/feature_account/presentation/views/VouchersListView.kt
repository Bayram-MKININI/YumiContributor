package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.*
import net.noliaware.yumi_contributor.feature_account.presentation.adapters.VoucherAdapter

class VouchersListView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var backView: View
    private lateinit var titleTextView: TextView
    private lateinit var recyclerView: RecyclerView
    var voucherAdapter
        get() = recyclerView.adapter as VoucherAdapter
        set(adapter) {
            recyclerView.adapter = adapter
        }
    var callback: VouchersListViewCallback? by weak()

    interface VouchersListViewCallback {
        fun onBackButtonClicked()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {

        backView = findViewById(R.id.back_view)
        backView.setOnClickListener {
            callback?.onBackButtonClicked()
        }

        titleTextView = findViewById(R.id.title_text_view)
        recyclerView = findViewById(R.id.recycler_view)

        recyclerView.also {
            it.layoutManager = LinearLayoutManager(context)
            it.addItemDecoration(MarginItemDecoration(convertDpToPx(20)))
        }
    }

    fun setTitle(title: String) {
        titleTextView.text = title
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        backView.measureWrapContent()

        titleTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 9 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val recyclerViewHeight =
            viewHeight - (backView.measuredHeight + titleTextView.measuredHeight + getStatusBarHeight() + convertDpToPx(
                35
            ))

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

        backView.layoutToTopLeft(convertDpToPx(10), getStatusBarHeight() + convertDpToPx(10))

        titleTextView.layoutToTopLeft(
            (viewWidth - titleTextView.measuredWidth) / 2,
            backView.bottom + convertDpToPx(10)
        )

        recyclerView.layoutToTopLeft(0, titleTextView.bottom + convertDpToPx(10))
    }
}