package net.noliaware.yumi_contributor.feature_message.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.getStatusBarHeight
import net.noliaware.yumi_contributor.commun.util.layoutToBottomLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.weak

class ReadMailView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var headerView: View
    private lateinit var messageIconView: View
    private lateinit var backView: View
    private lateinit var backgroundView: View
    private lateinit var contentView: View
    private lateinit var deleteIconView: View
    private lateinit var titleTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var messageParentView: View
    private lateinit var messageTextView: TextView
    private lateinit var composeButton: View

    var callback: ReadMailViewCallback? by weak()

    interface ReadMailViewCallback {
        fun onBackButtonClicked()
        fun onDeleteButtonClicked()
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

        headerView = findViewById(R.id.header_view)
        messageIconView = findViewById(R.id.message_icon_view)
        backView = findViewById(R.id.back_view)
        backView.setOnClickListener(onClickListener)
        backgroundView = findViewById(R.id.background_view)

        deleteIconView = findViewById(R.id.delete_icon_view)
        deleteIconView.setOnClickListener(onClickListener)

        contentView = findViewById(R.id.content_layout)
        titleTextView = contentView.findViewById(R.id.title_text_view)
        timeTextView = contentView.findViewById(R.id.time_text_view)

        messageParentView = contentView.findViewById(R.id.message_parent_view)
        messageTextView = messageParentView.findViewById(R.id.message_text_view)

        composeButton = findViewById(R.id.compose_layout)
        composeButton.setOnClickListener(onClickListener)
    }

    private val onClickListener: OnClickListener by lazy {
        OnClickListener {
            when (it.id) {
                R.id.back_view -> callback?.onBackButtonClicked()
                R.id.delete_icon_view -> callback?.onDeleteButtonClicked()
                R.id.compose_layout -> callback?.onComposeButtonClicked()
            }
        }
    }

    fun fillViewWithData(readMailViewAdapter: ReadMailViewAdapter) {
        titleTextView.text = readMailViewAdapter.subject
        timeTextView.text = readMailViewAdapter.time
        messageTextView.text = readMailViewAdapter.message
        composeButton.isVisible = readMailViewAdapter.replyPossible
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

        backView.measureWrapContent()
        messageIconView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(50), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(50), MeasureSpec.EXACTLY)
        )

        backgroundView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight - getStatusBarHeight(), MeasureSpec.EXACTLY)
        )

        deleteIconView.measureWrapContent()

        val contentViewHeight = viewHeight - (headerView.measuredHeight + messageIconView.measuredHeight / 2 +
                    convertDpToPx(25))

        val contentViewWidth = viewWidth * 9 / 10
        contentView.measure(
            MeasureSpec.makeMeasureSpec(contentViewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(contentViewHeight, MeasureSpec.EXACTLY)
        )

        titleTextView.measure(
            MeasureSpec.makeMeasureSpec(contentViewWidth * 9 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        timeTextView.measureWrapContent()

        val messageParentViewHeight =
            contentView.height - (titleTextView.measuredHeight + timeTextView.measuredHeight +
                    convertDpToPx(55))

        messageParentView.measure(
            MeasureSpec.makeMeasureSpec(contentViewWidth * 9 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(messageParentViewHeight, MeasureSpec.EXACTLY)
        )

        if (composeButton.isVisible) {
            composeButton.measureWrapContent()
            val availableSpaceForMessage = messageParentView.measuredHeight - (composeButton.measuredHeight + convertDpToPx(30))
            val extraPadding = messageTextView.measuredHeight - availableSpaceForMessage
            if (extraPadding > 0) {
                messageParentView.updatePadding(bottom = extraPadding)
            }
        }

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        val viewWidth = right - left
        val viewHeight = bottom - top

        backgroundView.layoutToTopLeft(
            0,
            getStatusBarHeight()
        )

        headerView.layoutToTopLeft(0, 0)

        backView.layoutToTopLeft(
            convertDpToPx(10),
            getStatusBarHeight() + convertDpToPx(10)
        )

        messageIconView.layoutToTopLeft(
            (viewWidth - messageIconView.measuredWidth) / 2,
            headerView.bottom - messageIconView.measuredHeight / 2
        )

        contentView.layoutToTopLeft(
            (viewWidth - contentView.measuredWidth) / 2,
            messageIconView.bottom + convertDpToPx(15)
        )

        val marginLeft = contentView.measuredWidth * 1 / 20
        titleTextView.layoutToTopLeft(
            marginLeft,
            convertDpToPx(20)
        )

        timeTextView.layoutToTopLeft(
            marginLeft,
            titleTextView.bottom + convertDpToPx(5)
        )

        messageParentView.layoutToTopLeft(
            marginLeft,
            timeTextView.bottom + convertDpToPx(15)
        )

        deleteIconView.layoutToTopRight(
            contentView.right - convertDpToPx(20),
            contentView.top - deleteIconView.measuredHeight / 2
        )

        if (composeButton.isVisible) {
            composeButton.layoutToBottomLeft(
                (viewWidth - composeButton.measuredWidth) / 2,
                bottom - convertDpToPx(40)
            )
        }
    }
}