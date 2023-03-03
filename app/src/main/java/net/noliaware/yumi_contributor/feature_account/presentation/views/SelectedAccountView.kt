package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.views.ClipartTabView
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.expandViewHitArea
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.weak

class SelectedAccountView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var backImageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var availableTabView: ClipartTabView
    private lateinit var usedTabView: ClipartTabView
    private lateinit var cancelledTabView: ClipartTabView
    private lateinit var contentView: View
    private lateinit var viewPager: ViewPager2

    val getViewPager get() = viewPager
    var callback: SelectedAccountViewCallback? by weak()

    fun interface SelectedAccountViewCallback {
        fun onBackButtonClicked()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        backImageView = findViewById(R.id.back_image_view)
        post {
            backImageView.expandViewHitArea()
        }
        backImageView.setOnClickListener { callback?.onBackButtonClicked() }
        titleTextView = findViewById(R.id.title_text_view)
        availableTabView = findViewById(R.id.available_tab_layout)
        availableTabView.setTitle(context.getString(R.string.available).uppercase())
        availableTabView.setOnClickListener {
            setFirstTabSelected()
            viewPager.setCurrentItem(0, true)
        }
        usedTabView = findViewById(R.id.used_tab_layout)
        usedTabView.setTitle(context.getString(R.string.used).uppercase())
        usedTabView.setOnClickListener {
            setSecondTabSelected()
            viewPager.setCurrentItem(1, true)
        }
        cancelledTabView = findViewById(R.id.cancelled_tab_layout)
        cancelledTabView.setTitle(context.getString(R.string.cancelled).uppercase())
        cancelledTabView.setOnClickListener {
            setThirdTabSelected()
            viewPager.setCurrentItem(2, true)
        }
        contentView = findViewById(R.id.content_layout)
        viewPager = contentView.findViewById(R.id.pager)
        viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> {
                        setFirstTabSelected()
                    }
                    1 -> {
                        setSecondTabSelected()
                    }
                    else -> {
                        setThirdTabSelected()
                    }
                }
            }
        })
    }

    fun setTitle(title: String) {
        titleTextView.text = title
    }

    private fun setFirstTabSelected() {
        availableTabView.setTabSelected(true)
        usedTabView.setTabSelected(false)
        cancelledTabView.setTabSelected(false)
    }

    private fun setSecondTabSelected() {
        availableTabView.setTabSelected(false)
        usedTabView.setTabSelected(true)
        cancelledTabView.setTabSelected(false)
    }

    private fun setThirdTabSelected() {
        availableTabView.setTabSelected(false)
        usedTabView.setTabSelected(false)
        cancelledTabView.setTabSelected(true)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        backImageView.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(convertDpToPx(14), MeasureSpec.EXACTLY)
        )

        titleTextView.measureWrapContent()

        availableTabView.measureWrapContent()
        usedTabView.measureWrapContent()
        cancelledTabView.measureWrapContent()

        val contentViewWidth = viewWidth * 9 / 10

        val tabWidthExtra = (contentViewWidth - (availableTabView.measuredWidth + usedTabView.measuredWidth +
                    cancelledTabView.measuredWidth + convertDpToPx(16))) / 3

        availableTabView.measure(
            MeasureSpec.makeMeasureSpec(
                availableTabView.measuredWidth + tabWidthExtra,
                MeasureSpec.EXACTLY
            ),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        usedTabView.measure(
            MeasureSpec.makeMeasureSpec(
                usedTabView.measuredWidth + tabWidthExtra,
                MeasureSpec.EXACTLY
            ),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        cancelledTabView.measure(
            MeasureSpec.makeMeasureSpec(
                cancelledTabView.measuredWidth + tabWidthExtra,
                MeasureSpec.EXACTLY
            ),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val contentViewHeight = viewHeight - (titleTextView.measuredHeight + availableTabView.measuredHeight
                    + convertDpToPx(50))

        contentView.measure(
            MeasureSpec.makeMeasureSpec(contentViewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(contentViewHeight, MeasureSpec.EXACTLY)
        )

        viewPager.measure(
            MeasureSpec.makeMeasureSpec(contentView.measuredWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(contentView.measuredHeight, MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        backImageView.layoutToTopLeft(
            convertDpToPx(15),
            convertDpToPx(15)
        )

        titleTextView.layoutToTopLeft(
            (viewWidth - titleTextView.measuredWidth) / 2,
            convertDpToPx(15)
        )

        val contentViewLeft = (viewWidth - contentView.measuredWidth) / 2
        availableTabView.layoutToTopLeft(
            contentViewLeft,
            titleTextView.bottom + convertDpToPx(15)
        )

        usedTabView.layoutToTopLeft(
            availableTabView.right + convertDpToPx(8),
            availableTabView.top
        )

        cancelledTabView.layoutToTopLeft(
            usedTabView.right + convertDpToPx(8),
            availableTabView.top
        )

        contentView.layoutToTopLeft(
            (viewWidth - contentView.measuredWidth) / 2,
            availableTabView.bottom - convertDpToPx(20)
        )

        viewPager.layoutToTopLeft(0, 0)
    }
}