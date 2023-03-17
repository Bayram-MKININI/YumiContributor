package net.noliaware.yumi_contributor.feature_message.presentation.views

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.text.InputType
import android.util.AttributeSet
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.getStatusBarHeight
import net.noliaware.yumi_contributor.commun.util.layoutToBottomRight
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.weak

class SendMailView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var headerView: View
    private lateinit var messageIconView: View
    private lateinit var backView: View
    private lateinit var backgroundView: View
    private lateinit var contentView: View
    private lateinit var titleTextView: TextView
    private lateinit var messageBackgroundView: View
    private lateinit var subjectEditText: EditText
    private lateinit var chevronImageView: ImageView
    private lateinit var separatorLineView: View
    private lateinit var messageParentLayout: View
    private lateinit var mailEditText: EditText
    private lateinit var sendButton: View
    private var isSubjectFixed: Boolean = false
    private val visibleRect = Rect()

    var callback: SendMailViewCallback? by weak()

    interface SendMailViewCallback {
        fun onBackButtonClicked()
        fun onSubjectEditTextClicked()
        fun onClearButtonClicked()
        fun onSendMailClicked(text: String)
    }

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
        contentView = findViewById(R.id.content_layout)

        titleTextView = contentView.findViewById(R.id.title_text_view)
        messageBackgroundView = contentView.findViewById(R.id.message_background)

        subjectEditText = contentView.findViewById(R.id.subject_edit_text)
        subjectEditText.setRawInputType(InputType.TYPE_NULL)
        subjectEditText.setOnConsistentClickListener {
            if (!isSubjectFixed) {
                callback?.onSubjectEditTextClicked()
            }
        }

        chevronImageView = contentView.findViewById(R.id.chevron_image_view)
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

    private fun EditText.setOnConsistentClickListener(doOnClick: (View) -> Unit) {
        val gestureDetector = GestureDetectorCompat(context, object : SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                doOnClick(this@setOnConsistentClickListener)
                return false
            }
        })

        this.setOnTouchListener { _, motionEvent -> gestureDetector.onTouchEvent(motionEvent) }
    }

    fun setSubject(subject: String) {
        subjectEditText.text.clear()
        subjectEditText.setText(subject)
    }

    fun setSubjectFixed(subject: String) {
        setSubject(subject)
        isSubjectFixed = true
        chevronImageView.isGone = true
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
        val messageBackgroundViewHeight = contentView.measuredHeight - (titleTextView.measuredHeight + sendButton.measuredHeight / 2 +
                    if (visibleRect.height() == screenHeight) {
                        convertDpToPx(40)
                    } else {
                        screenHeight - visibleRect.height() + convertDpToPx(25)
                    })

        messageBackgroundView.measure(
            MeasureSpec.makeMeasureSpec(contentView.measuredWidth * 95 / 100, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(messageBackgroundViewHeight, MeasureSpec.EXACTLY)
        )

        chevronImageView.measureWrapContent()

        val subjectWidth = messageBackgroundView.measuredWidth - convertDpToPx(30)
        subjectEditText.measure(
            MeasureSpec.makeMeasureSpec(
                subjectWidth - chevronImageView.measuredWidth,
                MeasureSpec.EXACTLY
            ),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        separatorLineView.measure(
            MeasureSpec.makeMeasureSpec(subjectWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(3), MeasureSpec.EXACTLY)
        )

        val availableHeightForBody = messageBackgroundView.measuredHeight - (subjectEditText.measuredHeight +
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

        titleTextView.layoutToTopLeft(
            (contentView.measuredWidth - titleTextView.measuredWidth) / 2,
            convertDpToPx(15)
        )

        messageBackgroundView.layoutToTopLeft(
            (contentView.measuredWidth - messageBackgroundView.measuredWidth) / 2,
            titleTextView.bottom + convertDpToPx(15)
        )

        subjectEditText.layoutToTopLeft(
            messageBackgroundView.left + convertDpToPx(15),
            messageBackgroundView.top + convertDpToPx(20)
        )

        chevronImageView.layoutToTopRight(
            messageBackgroundView.right - convertDpToPx(15),
            subjectEditText.top + (subjectEditText.measuredHeight - chevronImageView.measuredHeight) / 2
        )

        separatorLineView.layoutToTopLeft(
            (contentView.measuredWidth - separatorLineView.measuredWidth) / 2,
            subjectEditText.bottom + convertDpToPx(10)
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