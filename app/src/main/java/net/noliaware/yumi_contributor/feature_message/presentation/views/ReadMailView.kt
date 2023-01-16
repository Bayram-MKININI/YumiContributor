package net.noliaware.yumi_contributor.feature_message.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.*

class ReadMailView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var backView: View
    private lateinit var titleTextView: TextView
    private lateinit var messageParentView: View
    private lateinit var readMailContentView: ReadMailContentView
    private lateinit var composeButton: View

    var callback: ReadMailViewCallback? by weak()

    interface ReadMailViewCallback {
        fun onBackButtonClicked()
        fun onComposeButtonClicked()
    }

    data class ReadMailViewAdapter(
        val subject: String = "",
        val time: String = "",
        val message: String = "",
        val replyPossible: Boolean = false
    )

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
        messageParentView = findViewById(R.id.message_parent_view)
        readMailContentView = messageParentView.findViewById(R.id.read_mail_content_view)

        composeButton = findViewById(R.id.compose_fab)
        composeButton.setOnClickListener {
            callback?.onComposeButtonClicked()
        }
    }

    fun fillViewWithData(readMailViewAdapter: ReadMailViewAdapter) {
        titleTextView.text = readMailViewAdapter.subject
        readMailContentView.timeTextView.text = readMailViewAdapter.time
        readMailContentView.messageTextView.text = readMailViewAdapter.message
        composeButton.isVisible = readMailViewAdapter.replyPossible
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        backView.measureWrapContent()

        titleTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth - convertDpToPx(40), MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val readMailContentViewHeight =
            viewHeight - (backView.measuredHeight + titleTextView.measuredHeight + getStatusBarHeight() + convertDpToPx(
                60
            ))

        messageParentView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth - convertDpToPx(40), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(readMailContentViewHeight, MeasureSpec.EXACTLY)
        )

        composeButton.measureWrapContent()

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        val viewWidth = right - left
        val viewHeight = bottom - top

        backView.layoutToTopLeft(
            convertDpToPx(10),
            getStatusBarHeight() + convertDpToPx(10)
        )

        titleTextView.layoutToTopLeft(
            (viewWidth - titleTextView.measuredWidth) / 2,
            backView.bottom + convertDpToPx(10)
        )

        messageParentView.layoutToTopLeft(
            (viewWidth - readMailContentView.measuredWidth) / 2,
            titleTextView.bottom + convertDpToPx(20)
        )

        composeButton.layoutToBottomRight(
            viewWidth - convertDpToPx(20),
            viewHeight - convertDpToPx(20)
        )
    }
}