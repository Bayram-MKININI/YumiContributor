package net.noliaware.yumi_contributor.feature_message.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import java.lang.Integer.max

class MessageItemView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var iconImageView: ImageView
    private lateinit var subjectTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var bodyTextView: TextView

    data class MessageItemViewAdapter(
        @DrawableRes
        val priorityIconRes: Int,
        val subject: String = "",
        val time: String = "",
        val body: String = ""
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        iconImageView = findViewById(R.id.icon_image_view)
        subjectTextView = findViewById(R.id.subject_text_view)
        timeTextView = findViewById(R.id.time_text_view)
        bodyTextView = findViewById(R.id.body_text_view)
    }

    fun fillViewWithData(messageItemViewAdapter: MessageItemViewAdapter) {
        iconImageView.setImageResource(messageItemViewAdapter.priorityIconRes)
        subjectTextView.text = messageItemViewAdapter.subject
        timeTextView.text = messageItemViewAdapter.time
        bodyTextView.text = messageItemViewAdapter.body
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        iconImageView.measureWrapContent()
        timeTextView.measureWrapContent()

        val bodyTextMaxWidth = viewWidth - (timeTextView.measuredWidth + iconImageView.measuredWidth +
                convertDpToPx(32))
        subjectTextView.measure(
            MeasureSpec.makeMeasureSpec(bodyTextMaxWidth, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        bodyTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 9 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        viewHeight = bodyTextView.measuredHeight + max(
            subjectTextView.measuredHeight,
            timeTextView.measuredHeight
        ) + convertDpToPx(35)

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
            convertDpToPx(10)
        )

        subjectTextView.layoutToTopLeft(
            iconImageView.right + convertDpToPx(2),
            convertDpToPx(15)
        )

        timeTextView.layoutToTopRight(
            viewWidth - convertDpToPx(15),
            subjectTextView.top
        )

        bodyTextView.layoutToTopLeft(
            subjectTextView.left,
            max(subjectTextView.bottom, timeTextView.bottom) + convertDpToPx(5)
        )
    }
}