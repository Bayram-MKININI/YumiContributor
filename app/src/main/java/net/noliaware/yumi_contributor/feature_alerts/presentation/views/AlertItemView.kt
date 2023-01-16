package net.noliaware.yumi_contributor.feature_alerts.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.feature_alerts.domain.model.AlertPriority

class AlertItemView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var backgroundView: View
    private lateinit var priorityImageView: ImageView
    private lateinit var timeTextView: TextView
    private lateinit var bodyTextView: TextView

    data class AlertItemViewAdapter(
        val priority: AlertPriority,
        val time: String,
        val body: String
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        backgroundView = findViewById(R.id.background_view)
        priorityImageView = findViewById(R.id.priority_image_view)
        timeTextView = findViewById(R.id.time_text_view)
        bodyTextView = findViewById(R.id.body_text_view)
    }

    fun fillViewWithData(alertItemViewAdapter: AlertItemViewAdapter) {

        timeTextView.text = alertItemViewAdapter.time
        bodyTextView.text = alertItemViewAdapter.body

        when (alertItemViewAdapter.priority) {
            AlertPriority.IMPORTANT -> {
                priorityImageView.isVisible = true
                priorityImageView.setBackgroundResource(R.drawable.ring_filled_orange)
                priorityImageView.setImageResource(R.drawable.ic_warning)
            }
            AlertPriority.CRITICAL -> {
                priorityImageView.isVisible = true
                priorityImageView.setBackgroundResource(R.drawable.ring_filled_red)
                priorityImageView.setImageResource(R.drawable.ic_danger)
            }
            else -> priorityImageView.isGone = true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        priorityImageView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(32), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(32), MeasureSpec.EXACTLY)
        )

        timeTextView.measureWrapContent()

        val bodyTextViewWidth = viewWidth - (priorityImageView.measuredWidth * 2 - convertDpToPx(8))

        bodyTextView.measure(
            MeasureSpec.makeMeasureSpec(bodyTextViewWidth, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        viewHeight =
            priorityImageView.measuredHeight + timeTextView.measuredHeight + bodyTextView.measuredHeight + convertDpToPx(
                40
            )

        backgroundView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth - convertDpToPx(8), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight - convertDpToPx(5), MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        if (priorityImageView.isVisible)
            priorityImageView.layoutToTopLeft(0, 0)

        backgroundView.layoutToTopLeft(convertDpToPx(4), convertDpToPx(4))

        val edgeSpace = priorityImageView.measuredWidth - convertDpToPx(4)

        var timeTextViewTop = priorityImageView.bottom - convertDpToPx(4)

        if (priorityImageView.isGone)
            timeTextViewTop = edgeSpace

        var timeTextViewLeft = priorityImageView.right - convertDpToPx(4)

        if (priorityImageView.isGone)
            timeTextViewLeft = edgeSpace

        timeTextView.layoutToTopLeft(
            timeTextViewTop,
            timeTextViewLeft
        )

        bodyTextView.layoutToTopLeft(
            backgroundView.left + (backgroundView.measuredWidth - bodyTextView.measuredWidth) / 2,
            timeTextView.bottom + convertDpToPx(10)
        )
    }
}