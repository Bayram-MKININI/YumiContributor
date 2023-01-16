package net.noliaware.yumi_contributor.feature_message.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent

class MessageItemView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var subjectTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var bodyTextView: TextView

    data class MessageItemViewAdapter(
        val subject: String = "",
        val time: String = "",
        val body: String = ""
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        subjectTextView = findViewById(R.id.subject_text_view)
        timeTextView = findViewById(R.id.time_text_view)
        bodyTextView = findViewById(R.id.body_text_view)
    }

    fun fillViewWithData(messageItemViewAdapter: MessageItemViewAdapter) {
        subjectTextView.text = messageItemViewAdapter.subject
        timeTextView.text = messageItemViewAdapter.time
        bodyTextView.text = messageItemViewAdapter.body
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        timeTextView.measureWrapContent()

        val bodyTextMaxWidth = viewWidth - convertDpToPx(48)
        bodyTextView.measure(
            MeasureSpec.makeMeasureSpec(bodyTextMaxWidth, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        subjectTextView.measure(
            MeasureSpec.makeMeasureSpec(
                bodyTextMaxWidth - timeTextView.measuredWidth,
                MeasureSpec.AT_MOST
            ),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        viewHeight =
            subjectTextView.measuredHeight + bodyTextView.measuredHeight + convertDpToPx(70)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        val bodyTextMaxWidth = viewWidth - convertDpToPx(48)
        val marginLeft = (viewWidth - bodyTextMaxWidth) / 2

        subjectTextView.layoutToTopLeft(
            marginLeft,
            (viewHeight - (subjectTextView.measuredHeight + bodyTextView.measuredHeight + convertDpToPx(
                10
            ))) / 2
        )

        val timeTextViewRight = viewWidth - marginLeft
        val timeTextViewLeft = timeTextViewRight - timeTextView.measuredWidth
        val timeTextViewBottom = subjectTextView.bottom
        val timeTextViewTop = timeTextViewBottom - timeTextView.measuredHeight

        timeTextView.layout(
            timeTextViewLeft,
            timeTextViewTop,
            timeTextViewRight,
            timeTextViewBottom
        )

        bodyTextView.layoutToTopLeft(
            marginLeft,
            subjectTextView.bottom + convertDpToPx(10)
        )
    }
}