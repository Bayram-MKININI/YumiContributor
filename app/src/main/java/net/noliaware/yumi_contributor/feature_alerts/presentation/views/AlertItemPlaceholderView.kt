package net.noliaware.yumi_contributor.feature_alerts.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft

class AlertItemPlaceholderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var iconView: View
    private lateinit var timeView: View
    private lateinit var bodyView: View

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        iconView = findViewById(R.id.icon_view)
        timeView = findViewById(R.id.time_view)
        bodyView = findViewById(R.id.body_view)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        iconView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(20), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(20), MeasureSpec.EXACTLY)
        )
        timeView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 4 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(15), MeasureSpec.EXACTLY)
        )

        val bodyTextViewWidth = viewWidth * 9 / 10 - (iconView.measuredWidth + convertDpToPx(5))
        bodyView.measure(
            MeasureSpec.makeMeasureSpec(bodyTextViewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(15), MeasureSpec.EXACTLY)
        )

        viewHeight = timeView.measuredHeight + bodyView.measuredHeight + convertDpToPx(30)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        iconView.layoutToTopLeft(
            convertDpToPx(10),
            convertDpToPx(10)
        )

        timeView.layoutToTopLeft(
            iconView.right + convertDpToPx(5),
            iconView.top
        )

        bodyView.layoutToTopLeft(
            timeView.left,
            timeView.bottom + convertDpToPx(10)
        )
    }
}