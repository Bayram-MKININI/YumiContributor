package net.noliaware.yumi_contributor.feature_alerts.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent

class AlertItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var iconImageView: ImageView
    private lateinit var timeTextView: TextView
    private lateinit var bodyTextView: TextView

    data class AlertItemViewAdapter(
        @DrawableRes
        val priorityIconRes: Int?,
        val time: String,
        val body: String
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        iconImageView = findViewById(R.id.icon_image_view)
        timeTextView = findViewById(R.id.time_text_view)
        bodyTextView = findViewById(R.id.body_text_view)
    }

    fun fillViewWithData(alertItemViewAdapter: AlertItemViewAdapter) {
        alertItemViewAdapter.priorityIconRes?.let { iconImageView.setImageResource(it) }
        timeTextView.text = alertItemViewAdapter.time
        bodyTextView.text = alertItemViewAdapter.body
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        iconImageView.measureWrapContent()

        timeTextView.measureWrapContent()

        val bodyTextViewWidth = viewWidth * 9 / 10 - (iconImageView.measuredWidth + convertDpToPx(2))
        bodyTextView.measure(
            MeasureSpec.makeMeasureSpec(bodyTextViewWidth, MeasureSpec.AT_MOST),
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

        iconImageView.layoutToTopLeft(
            convertDpToPx(5),
            convertDpToPx(5)
        )

        timeTextView.layoutToTopLeft(
            iconImageView.right + convertDpToPx(2),
            convertDpToPx(10)
        )

        bodyTextView.layoutToTopLeft(
            timeTextView.left,
            timeTextView.bottom + convertDpToPx(10)
        )
    }
}