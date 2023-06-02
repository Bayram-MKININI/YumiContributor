package net.noliaware.yumi_contributor.feature_message.presentation.views

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.getStatusBarHeight
import net.noliaware.yumi_contributor.commun.util.layoutToBottomRight
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.weak

class SendMailView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var backgroundView: View
    private lateinit var headerView: View
    private lateinit var messageIconView: View
    private lateinit var backView: View
    private lateinit var contentView: View
    private lateinit var titleTextView: TextView
    private lateinit var messageBackgroundView: View
    lateinit var subjectSpinner: Spinner
        private set
    private lateinit var fixedSubjectTextView: TextView
    lateinit var prioritySpinner: Spinner
        private set
    private lateinit var separatorLineView: View
    private lateinit var messageParentLayout: View
    private lateinit var mailEditText: EditText
    private lateinit var sendButton: View
    private val visibleRect = Rect()

    var callback: SendMailViewCallback? by weak()

    interface SendMailViewCallback {
        fun onBackButtonClicked()
        fun onClearButtonClicked()
        fun onSendMailClicked(text: String)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {

        backgroundView = findViewById(R.id.background_view)
        headerView = findViewById(R.id.header_view)
        messageIconView = findViewById(R.id.message_icon_view)
        backView = findViewById(R.id.back_view)
        backView.setOnClickListener(onClickListener)
        contentView = findViewById(R.id.content_layout)

        titleTextView = contentView.findViewById(R.id.title_text_view)
        messageBackgroundView = contentView.findViewById(R.id.message_background)
        prioritySpinner = contentView.findViewById(R.id.priority_spinner)

        subjectSpinner = contentView.findViewById(R.id.subject_spinner)
        fixedSubjectTextView = contentView.findViewById(R.id.fixed_subject_text_view)
        separatorLineView = contentView.findViewById(R.id.separator_line_view)
        messageParentLayout = contentView.findViewById(R.id.message_parent_layout)
        mailEditText = messageParentLayout.findViewById(R.id.mail_edit_text)

        sendButton = findViewById(R.id.send_icon_view)
        sendButton.setOnClickListener(onClickListener)

        ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                messageParentLayout.requestLayout()
            }
            insets
        }
    }

    private val onClickListener: OnClickListener by lazy {
        OnClickListener {
            when (it.id) {
                R.id.back_view -> callback?.onBackButtonClicked()
                R.id.send_icon_view -> callback?.onSendMailClicked(mailEditText.text.toString())
            }
        }
    }

    fun setSelectedPriorityAtIndex(index: Int) {
        prioritySpinner.setSelection(index)
    }

    fun getSelectedPriorityIndex() = prioritySpinner.selectedItemPosition

    fun setSubjectFixed(subject: String) {
        subjectSpinner.isGone = true
        fixedSubjectTextView.isVisible = true
        fixedSubjectTextView.text = subject
    }

    fun getSelectedSubjectIndex() =
        if (subjectSpinner.selectedItemPosition < subjectSpinner.adapter.count) {
            subjectSpinner.selectedItemPosition
        } else {
            -1
        }

    fun computeMailView() {
        post {
            mailEditText.requestLayout()
        }
    }

    fun clearMail() {
        mailEditText.text.clear()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        backgroundView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight - getStatusBarHeight(), MeasureSpec.EXACTLY)
        )

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

        val contentViewHeight = viewHeight - (headerView.measuredHeight + messageIconView.measuredHeight / 2 +
                convertDpToPx(25))

        val contentViewWidth = viewWidth * 95 / 100
        contentView.measure(
            MeasureSpec.makeMeasureSpec(contentViewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(contentViewHeight, MeasureSpec.EXACTLY)
        )

        titleTextView.measureWrapContent()

        getWindowVisibleDisplayFrame(visibleRect)

        sendButton.measureWrapContent()

        val screenHeight = viewHeight - getStatusBarHeight()
        val messageBackgroundViewHeight =
            contentView.measuredHeight - (titleTextView.measuredHeight + sendButton.measuredHeight / 2 +
                    if (visibleRect.height() == screenHeight) {
                        convertDpToPx(40)
                    } else {
                        contentView.measuredHeight - visibleRect.height() + convertDpToPx(30) + (viewWidth * 5 / 200)
                    })

        messageBackgroundView.measure(
            MeasureSpec.makeMeasureSpec(contentView.measuredWidth * 95 / 100, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(messageBackgroundViewHeight, MeasureSpec.EXACTLY)
        )

        val subjectWidth = messageBackgroundView.measuredWidth - convertDpToPx(30)

        prioritySpinner.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(50), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val subjectSpinnerWidth = subjectWidth - prioritySpinner.measuredWidth

        if (subjectSpinner.isVisible) {
            subjectSpinner.measure(
                MeasureSpec.makeMeasureSpec(subjectSpinnerWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
        }

        if (fixedSubjectTextView.isVisible) {
            fixedSubjectTextView.measure(
                MeasureSpec.makeMeasureSpec(subjectSpinnerWidth, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
        }

        separatorLineView.measure(
            MeasureSpec.makeMeasureSpec(subjectWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(3), MeasureSpec.EXACTLY)
        )

        val availableHeightForBody =
            messageBackgroundView.measuredHeight - (subjectSpinner.measuredHeight +
                    separatorLineView.measuredHeight + convertDpToPx(50))

        mailEditText.minHeight = availableHeightForBody

        messageParentLayout.measure(
            MeasureSpec.makeMeasureSpec(subjectWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(availableHeightForBody, MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        val viewWidth = right - left
        val viewHeight = bottom - top
        val screenHeight = viewHeight - getStatusBarHeight()

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

        val contentSideSpace = (viewWidth - contentView.measuredWidth) / 2
        val contentViewTop = if (visibleRect.height() == screenHeight) {
            messageIconView.bottom + convertDpToPx(15)
        } else {
            getStatusBarHeight() + contentSideSpace
        }
        contentView.layoutToTopLeft(
            contentSideSpace,
            contentViewTop
        )

        titleTextView.layoutToTopLeft(
            (contentView.measuredWidth - titleTextView.measuredWidth) / 2,
            convertDpToPx(15)
        )

        messageBackgroundView.layoutToTopLeft(
            (contentView.measuredWidth - messageBackgroundView.measuredWidth) / 2,
            titleTextView.bottom + convertDpToPx(15)
        )

        if (subjectSpinner.isVisible) {
            subjectSpinner.layoutToTopLeft(
                messageBackgroundView.left + convertDpToPx(15),
                messageBackgroundView.top + convertDpToPx(20)
            )
        }

        if (fixedSubjectTextView.isVisible) {
            fixedSubjectTextView.layoutToTopLeft(
                messageBackgroundView.left + convertDpToPx(15),
                messageBackgroundView.top + convertDpToPx(20)
            )
        }

        val prioritySpinnerTop = if (subjectSpinner.isVisible) {
            subjectSpinner.top + (subjectSpinner.measuredHeight - prioritySpinner.measuredHeight) / 2
        } else {
            fixedSubjectTextView.top + (fixedSubjectTextView.measuredHeight - prioritySpinner.measuredHeight) / 2
        }
        prioritySpinner.layoutToTopRight(
            messageBackgroundView.right - convertDpToPx(15),
            prioritySpinnerTop
        )

        val separatorLineViewCeil = listOf(
            subjectSpinner.bottom,
            fixedSubjectTextView.bottom,
            prioritySpinner.bottom
        ).max()
        separatorLineView.layoutToTopLeft(
            (contentView.measuredWidth - separatorLineView.measuredWidth) / 2,
            separatorLineViewCeil + convertDpToPx(5)
        )

        messageParentLayout.layoutToTopLeft(
            (contentView.measuredWidth - messageParentLayout.measuredWidth) / 2,
            separatorLineView.bottom
        )

        sendButton.layoutToBottomRight(
            messageBackgroundView.right,
            messageBackgroundView.bottom + sendButton.measuredHeight / 2
        )
    }
}