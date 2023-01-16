package net.noliaware.yumi_contributor.feature_message.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.*

class ReadMailContentView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var backgroundView: View

    lateinit var timeTextView: TextView
        private set

    lateinit var messageTextView: TextView
        private set

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        backgroundView = findViewById(R.id.background_view)
        timeTextView = findViewById(R.id.time_text_view)
        messageTextView = findViewById(R.id.message_text_view)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        timeTextView.measureWrapContent()

        messageTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth - convertDpToPx(30), MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val backgroundViewHeight = viewHeight.coerceAtLeast(
            timeTextView.measuredHeight + messageTextView.measuredHeight + getStatusBarHeight() + convertDpToPx(
                70
            )
        )

        backgroundView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(backgroundViewHeight, MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(backgroundViewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        val viewWidth = right - left
        val viewHeight = bottom - top

        backgroundView.layoutToTopLeft(0, 0)

        timeTextView.layoutToTopRight(
            viewWidth - convertDpToPx(15),
            backgroundView.top + convertDpToPx(30)
        )

        messageTextView.layoutToTopLeft(
            convertDpToPx(15),
            timeTextView.bottom + convertDpToPx(20)
        )
    }
}