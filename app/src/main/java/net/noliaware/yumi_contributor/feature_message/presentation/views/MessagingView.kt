package net.noliaware.yumi_contributor.feature_message.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.views.ClipartTabView
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.getStatusBarHeight
import net.noliaware.yumi_contributor.commun.util.layoutToBottomLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.removeOverScroll
import net.noliaware.yumi_contributor.commun.util.weak

class MessagingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var headerView: View
    private lateinit var titleTextView: TextView
    private lateinit var messageIconView: View
    private lateinit var receivedTabView: ClipartTabView
    private lateinit var sentTabView: ClipartTabView
    private lateinit var contentView: View
    private lateinit var viewPager: ViewPager2
    private lateinit var composeButton: View

    var callback: MailViewCallback? by weak()

    val getViewPager get() = viewPager

    fun interface MailViewCallback {
        fun onComposeButtonClicked()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {

        headerView = findViewById(R.id.header_view)
        titleTextView = findViewById(R.id.title_text_view)
        messageIconView = findViewById(R.id.message_icon_view)
        receivedTabView = findViewById(R.id.received_tab_layout)
        receivedTabView.setTitle(context.getString(R.string.received).uppercase())
        receivedTabView.setOnClickListener {
            setFirstTabSelected()
            viewPager.setCurrentItem(0, true)
        }
        sentTabView = findViewById(R.id.sent_tab_layout)
        sentTabView.setTitle(context.getString(R.string.sent).uppercase())
        sentTabView.setOnClickListener {
            setSecondTabSelected()
            viewPager.setCurrentItem(1, true)
        }
        contentView = findViewById(R.id.content_layout)
        viewPager = contentView.findViewById(R.id.pager)
        viewPager.removeOverScroll()
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    setFirstTabSelected()
                } else {
                    setSecondTabSelected()
                }
            }
        })

        composeButton = findViewById(R.id.compose_layout)
        composeButton.setOnClickListener {
            callback?.onComposeButtonClicked()
        }
    }

    private fun setFirstTabSelected() {
        sentTabView.setTabSelected(false)
        receivedTabView.setTabSelected(true)
    }

    private fun setSecondTabSelected() {
        receivedTabView.setTabSelected(false)
        sentTabView.setTabSelected(true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        headerView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(getStatusBarHeight() + convertDpToPx(75), MeasureSpec.EXACTLY)
        )

        titleTextView.measureWrapContent()
        messageIconView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(50), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(50), MeasureSpec.EXACTLY)
        )

        val contentViewWidth = viewWidth * 95 / 100
        val sideMargin = viewWidth * 5 / 100 / 2

        receivedTabView.measureWrapContent()
        sentTabView.measureWrapContent()

        val tabWidthExtra = (contentViewWidth - (receivedTabView.measuredWidth + sentTabView.measuredWidth +
                convertDpToPx(8))) / 2

        receivedTabView.measure(
            MeasureSpec.makeMeasureSpec(receivedTabView.measuredWidth + tabWidthExtra, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        sentTabView.measure(
            MeasureSpec.makeMeasureSpec(sentTabView.measuredWidth + tabWidthExtra, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val contentViewHeight = viewHeight - (headerView.measuredHeight + messageIconView.measuredHeight / 2 +
                receivedTabView.measuredHeight + sideMargin + convertDpToPx(15))

        contentView.measure(
            MeasureSpec.makeMeasureSpec(contentViewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(contentViewHeight, MeasureSpec.EXACTLY)
        )

        viewPager.measure(
            MeasureSpec.makeMeasureSpec(contentView.measuredWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(contentView.measuredHeight, MeasureSpec.EXACTLY)
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

        headerView.layoutToTopLeft(0, 0)

        titleTextView.layoutToTopLeft(
            (viewWidth - titleTextView.measuredWidth) / 2,
            getStatusBarHeight() + convertDpToPx(15)
        )

        messageIconView.layoutToTopLeft(
            (viewWidth - messageIconView.measuredWidth) / 2,
            headerView.bottom - messageIconView.measuredHeight / 2
        )

        val contentViewLeft = (viewWidth - contentView.measuredWidth) / 2
        receivedTabView.layoutToTopLeft(
            contentViewLeft,
            messageIconView.bottom + convertDpToPx(15)
        )

        sentTabView.layoutToTopLeft(
            receivedTabView.right + convertDpToPx(8),
            receivedTabView.top
        )

        contentView.layoutToTopLeft(
            (viewWidth - contentView.measuredWidth) / 2,
            receivedTabView.bottom - convertDpToPx(20)
        )

        viewPager.layoutToTopLeft(0, 0)

        composeButton.layoutToBottomLeft(
            (viewWidth - composeButton.measuredWidth) / 2,
            bottom - convertDpToPx(50)
        )
    }
}