package net.noliaware.yumi_contributor.feature_alerts.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.MarginItemDecoration
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.getStatusBarHeight
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.feature_alerts.presentation.adapters.AlertAdapter

class AlertsView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var headerView: View
    private lateinit var titleTextView: TextView
    private lateinit var notificationIconView: View
    private lateinit var descriptionTextView: TextView
    private lateinit var contentView: View
    private lateinit var recyclerView: RecyclerView

    var alertAdapter
        get() = recyclerView.adapter as AlertAdapter
        set(adapter) {
            recyclerView.adapter = adapter
        }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {

        headerView = findViewById(R.id.header_view)
        titleTextView = findViewById(R.id.title_text_view)
        notificationIconView = findViewById(R.id.notification_icon_view)
        descriptionTextView = findViewById(R.id.description_text_view)
        contentView = findViewById(R.id.content_layout)
        recyclerView = contentView.findViewById(R.id.recycler_view)

        recyclerView.also {
            it.layoutManager = LinearLayoutManager(context)
            it.addItemDecoration(MarginItemDecoration(convertDpToPx(16)))
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        headerView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(
                getStatusBarHeight() + convertDpToPx(75),
                MeasureSpec.EXACTLY
            )
        )

        titleTextView.measureWrapContent()
        notificationIconView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(50), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(50), MeasureSpec.EXACTLY)
        )

        descriptionTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 8 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val contentViewWidth = viewWidth * 95 / 100
        val sideMargin = viewWidth * 5 / 100 / 2
        val contentViewHeight = viewHeight - (headerView.measuredHeight + notificationIconView.measuredHeight / 2 +
                    descriptionTextView.measuredHeight + sideMargin + convertDpToPx(45))

        contentView.measure(
            MeasureSpec.makeMeasureSpec(contentViewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(contentViewHeight, MeasureSpec.EXACTLY)
        )

        recyclerView.measure(
            MeasureSpec.makeMeasureSpec(contentView.measuredWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(contentView.measuredHeight, MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        headerView.layoutToTopLeft(0, 0)

        titleTextView.layoutToTopLeft(
            (viewWidth - titleTextView.measuredWidth) / 2,
            getStatusBarHeight() + convertDpToPx(15)
        )

        notificationIconView.layoutToTopLeft(
            (viewWidth - notificationIconView.measuredWidth) / 2,
            headerView.bottom - notificationIconView.measuredHeight / 2
        )

        descriptionTextView.layoutToTopLeft(
            (viewWidth - descriptionTextView.measuredWidth) / 2,
            notificationIconView.bottom + convertDpToPx(10)
        )

        contentView.layoutToTopLeft(
            (viewWidth - contentView.measuredWidth) / 2,
            descriptionTextView.bottom + convertDpToPx(15)
        )

        recyclerView.layoutToTopLeft(0, 0)
    }
}