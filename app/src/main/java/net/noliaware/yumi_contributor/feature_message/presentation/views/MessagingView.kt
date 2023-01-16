package net.noliaware.yumi_contributor.feature_message.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.*

class MessagingView(context: Context, attrs: AttributeSet?) : CoordinatorLayout(context, attrs) {

    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var composeButton: View

    var callback: MailViewCallback? by weak()

    val getTabLayout get() = tabLayout
    val getViewPager get() = viewPager

    interface MailViewCallback {
        fun onComposeButtonClicked()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {

        titleTextView = findViewById(R.id.title_text_view)
        descriptionTextView = findViewById(R.id.description_text_view)
        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.pager)

        composeButton = findViewById(R.id.compose_fab)
        composeButton.setOnClickListener {
            callback?.onComposeButtonClicked()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        titleTextView.measureWrapContent()

        descriptionTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 8 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        tabLayout.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val recyclerViewHeight =
            viewHeight - (titleTextView.measuredHeight + descriptionTextView.measuredHeight + tabLayout.measuredHeight + getStatusBarHeight() + convertDpToPx(
                35
            ))

        viewPager.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(recyclerViewHeight, MeasureSpec.EXACTLY)
        )

        composeButton.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        titleTextView.layoutToTopLeft(
            (viewWidth - titleTextView.measuredWidth) / 2,
            getStatusBarHeight() + convertDpToPx(15)
        )

        descriptionTextView.layoutToTopLeft(
            (viewWidth - descriptionTextView.measuredWidth) / 2,
            titleTextView.bottom + convertDpToPx(10)
        )

        viewPager.layoutToBottomLeft(0, viewHeight)

        tabLayout.layoutToBottomLeft(0, viewPager.top)

        composeButton.layoutToBottomRight(
            viewWidth - convertDpToPx(20),
            viewHeight - convertDpToPx(20)
        )
    }
}