package net.noliaware.yumi_contributor.feature_message.presentation.views

import android.content.Context
import android.text.Spanned
import android.text.SpannedString
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.facebook.shimmer.ShimmerFrameLayout
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.views.FillableTextWidget
import net.noliaware.yumi_contributor.commun.util.activateShimmer
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.getColorCompat
import net.noliaware.yumi_contributor.commun.util.getFontFromResources
import net.noliaware.yumi_contributor.commun.util.getStatusBarHeight
import net.noliaware.yumi_contributor.commun.util.layoutToBottomLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.weak

class ReadMailView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var backgroundView: View
    private lateinit var headerView: View
    private lateinit var messageIconView: View
    private lateinit var backView: View
    private lateinit var contentView: View
    private lateinit var shimmerView: ShimmerFrameLayout
    private lateinit var deleteIconView: View
    private lateinit var iconPlaceholderView: View
    private lateinit var priorityIconImageView: ImageView
    private lateinit var titleFillableTextWidget: FillableTextWidget
    private lateinit var timeFillableTextWidget: FillableTextWidget
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
        @DrawableRes
        val priorityIconRes: Int,
        val subject: String = "",
        val time: String = "",
        val message: Spanned = SpannedString(""),
        val replyPossible: Boolean = false
    )

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

        deleteIconView = findViewById(R.id.delete_icon_view)
        deleteIconView.setOnClickListener(onClickListener)

        contentView = findViewById(R.id.content_layout)
        shimmerView = findViewById(R.id.shimmer_view)
        iconPlaceholderView = shimmerView.findViewById(R.id.icon_placeholder_view)
        priorityIconImageView = shimmerView.findViewById(R.id.priority_icon_image_view)

        titleFillableTextWidget = shimmerView.findViewById(R.id.title_fillable_text_view)
        titleFillableTextWidget.textView.apply {
            typeface = context.getFontFromResources(R.font.omnes_semibold_regular)
            setTextColor(context.getColorCompat(R.color.grey_2))
            textSize = 19f
        }
        titleFillableTextWidget.setFixedWidth(true)

        timeFillableTextWidget = shimmerView.findViewById(R.id.time_fillable_text_view)
        timeFillableTextWidget.textView.apply {
            typeface = context.getFontFromResources(R.font.omnes_regular)
            setTextColor(context.getColorCompat(R.color.grey_2))
            textSize = 14f
        }

        messageParentView = shimmerView.findViewById(R.id.message_parent_view)
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
        priorityIconImageView.apply {
            iconPlaceholderView.isGone = true
            setImageResource(readMailViewAdapter.priorityIconRes)
            isVisible = true
        }
        titleFillableTextWidget.setText(readMailViewAdapter.subject)
        timeFillableTextWidget.setText(readMailViewAdapter.time)
        messageTextView.text = readMailViewAdapter.message
        composeButton.isVisible = readMailViewAdapter.replyPossible
    }

    fun activateLoading(visible: Boolean) {
        shimmerView.activateShimmer(visible)
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

        deleteIconView.measureWrapContent()

        val contentViewHeight = viewHeight - (headerView.measuredHeight + messageIconView.measuredHeight / 2 +
                convertDpToPx(25))

        val contentViewWidth = viewWidth * 95 / 100
        contentView.measure(
            MeasureSpec.makeMeasureSpec(contentViewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(contentViewHeight, MeasureSpec.EXACTLY)
        )

        if (iconPlaceholderView.isVisible) {
            iconPlaceholderView.measure(
                MeasureSpec.makeMeasureSpec(convertDpToPx(22), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(convertDpToPx(22), MeasureSpec.EXACTLY)
            )
        }

        if (priorityIconImageView.isVisible) {
            priorityIconImageView.measureWrapContent()
        }

        val titleTextViewWidth = contentViewWidth * 95 / 100 - if (priorityIconImageView.isVisible) {
            priorityIconImageView.measuredWidth + convertDpToPx(2)
        } else {
            iconPlaceholderView.measuredWidth + convertDpToPx(8)
        }

        titleFillableTextWidget.measure(
            MeasureSpec.makeMeasureSpec(titleTextViewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(22), MeasureSpec.EXACTLY)
        )

        timeFillableTextWidget.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 5 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(15), MeasureSpec.EXACTLY)
        )

        val messageParentViewHeight = shimmerView.measuredHeight - (titleFillableTextWidget.measuredHeight +
                timeFillableTextWidget.measuredHeight + convertDpToPx(45))

        messageParentView.measure(
            MeasureSpec.makeMeasureSpec(contentViewWidth * 95 / 100, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(messageParentViewHeight, MeasureSpec.EXACTLY)
        )

        if (composeButton.isVisible) {
            composeButton.measureWrapContent()
            val availableSpaceForMessage = messageParentView.measuredHeight - (composeButton.measuredHeight +
                    convertDpToPx(30))
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

        if (iconPlaceholderView.isVisible) {
            iconPlaceholderView.layoutToTopLeft(
                convertDpToPx(5),
                convertDpToPx(19)
            )
        }

        if (priorityIconImageView.isVisible) {
            priorityIconImageView.layoutToTopLeft(
                convertDpToPx(5),
                convertDpToPx(19)
            )
        }

        titleFillableTextWidget.layoutToTopLeft(
            if (priorityIconImageView.isVisible) {
                priorityIconImageView.right + convertDpToPx(2)
            } else {
                iconPlaceholderView.right + convertDpToPx(8)
            },
            convertDpToPx(20)
        )

        timeFillableTextWidget.layoutToTopLeft(
            titleFillableTextWidget.left,
            titleFillableTextWidget.bottom + convertDpToPx(5)
        )

        messageParentView.layoutToTopLeft(
            (contentView.measuredWidth - messageParentView.measuredWidth) / 2,
            timeFillableTextWidget.bottom + convertDpToPx(10)
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