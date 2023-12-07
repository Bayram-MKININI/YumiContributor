package net.noliaware.yumi_contributor.feature_message.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.getFontFromResources
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import java.lang.Integer.max

class MessageItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var iconImageView: ImageView
    private lateinit var subjectTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var mailTextView: TextView
    private lateinit var bodyTextView: TextView

    private val openedTypeFace by lazy {
        context.getFontFromResources(R.font.omnes_regular)
    }

    private val notOpenedTypeFace by lazy {
        context.getFontFromResources(R.font.omnes_semibold_regular)
    }

    data class MessageItemViewAdapter(
        @DrawableRes
        val priorityIconRes: Int,
        val subject: String = "",
        val time: String = "",
        val mail: String = "",
        val body: String = "",
        val opened: Boolean = false
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        iconImageView = findViewById(R.id.icon_image_view)
        subjectTextView = findViewById(R.id.subject_text_view)
        timeTextView = findViewById(R.id.time_text_view)
        mailTextView = findViewById(R.id.mail_text_view)
        bodyTextView = findViewById(R.id.body_text_view)
    }

    fun fillViewWithData(messageItemViewAdapter: MessageItemViewAdapter) {
        iconImageView.setImageResource(messageItemViewAdapter.priorityIconRes)
        subjectTextView.text = messageItemViewAdapter.subject
        timeTextView.text = messageItemViewAdapter.time
        mailTextView.text = messageItemViewAdapter.mail
        bodyTextView.text = messageItemViewAdapter.body
        if (messageItemViewAdapter.opened) {
            subjectTextView.typeface = openedTypeFace
        } else {
            subjectTextView.typeface = notOpenedTypeFace
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        iconImageView.measureWrapContent()
        timeTextView.measureWrapContent()

        val subjectTextMaxWidth = viewWidth - (timeTextView.measuredWidth + iconImageView.measuredWidth +
                convertDpToPx(32))
        subjectTextView.measure(
            MeasureSpec.makeMeasureSpec(subjectTextMaxWidth, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        mailTextView.measureWrapContent()

        val bodyTextMaxWidth = viewWidth - (iconImageView.measuredWidth + convertDpToPx(22))
        bodyTextView.measure(
            MeasureSpec.makeMeasureSpec(bodyTextMaxWidth, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        bodyTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 9 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        viewHeight = mailTextView.measuredHeight + bodyTextView.measuredHeight + max(
            subjectTextView.measuredHeight,
            timeTextView.measuredHeight
        ) + convertDpToPx(40)

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

        mailTextView.layoutToTopLeft(
            subjectTextView.left,
            max(subjectTextView.bottom, timeTextView.bottom) + convertDpToPx(5)
        )

        bodyTextView.layoutToTopLeft(
            subjectTextView.left,
            mailTextView.bottom + convertDpToPx(5)
        )
    }
}