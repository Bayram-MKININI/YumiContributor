package net.noliaware.yumi_contributor.feature_alerts.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent

class AlertItemView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var timeTextView: TextView
    private lateinit var bodyTextView: TextView

    data class AlertItemViewAdapter(
        val time: String,
        val body: String
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        timeTextView = findViewById(R.id.time_text_view)
        bodyTextView = findViewById(R.id.body_text_view)
    }

    fun fillViewWithData(alertItemViewAdapter: AlertItemViewAdapter) {
        timeTextView.text = alertItemViewAdapter.time
        bodyTextView.text = alertItemViewAdapter.body
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        timeTextView.measureWrapContent()

        bodyTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 9 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        viewHeight = timeTextView.measuredHeight + bodyTextView.measuredHeight + convertDpToPx(30)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        val leftEdge = viewWidth * 1 / 20

        timeTextView.layoutToTopLeft(
            leftEdge,
            convertDpToPx(10)
        )

        bodyTextView.layoutToTopLeft(
            timeTextView.left,
            timeTextView.bottom + convertDpToPx(10)
        )
    }
}