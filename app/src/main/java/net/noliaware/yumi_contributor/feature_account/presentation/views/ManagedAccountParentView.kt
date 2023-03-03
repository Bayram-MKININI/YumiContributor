package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.getStatusBarHeight
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight
import net.noliaware.yumi_contributor.commun.util.measureWrapContent

class ManagedAccountParentView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var headerView: View
    private lateinit var helloTextView: TextView
    private lateinit var nameTextView: TextView
    private lateinit var accountImageView: ImageView
    private lateinit var accountBadgeTextView: TextView
    private lateinit var viewPager: ViewPager2

    val getViewPager get() = viewPager

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        headerView = findViewById(R.id.header_view)
        helloTextView = findViewById(R.id.hello_text_view)
        nameTextView = findViewById(R.id.name_text_view)
        accountImageView = findViewById(R.id.account_image_view)
        accountBadgeTextView = findViewById(R.id.account_badge_text_view)
        viewPager = findViewById(R.id.pager)
        viewPager.isUserInputEnabled = false
    }

    fun setUserData(
        helloText: String,
        userName: String,
        accountBadgeValue: String?,
    ) {
        helloTextView.text = helloText
        nameTextView.text = userName
        accountBadgeValue?.let {
            accountBadgeTextView.text = accountBadgeValue
            accountBadgeTextView.isVisible = true
        }
    }

    fun displayAccountListView(animated: Boolean = true) {
        viewPager.setCurrentItem(0, animated)
    }

    fun displaySelectedAccountView(animated: Boolean = true) {
        viewPager.setCurrentItem(1, animated)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        headerView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(
                getStatusBarHeight() + convertDpToPx(111), MeasureSpec.EXACTLY
            )
        )

        helloTextView.measureWrapContent()
        nameTextView.measureWrapContent()

        accountImageView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(50), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(50), MeasureSpec.EXACTLY)
        )

        accountBadgeTextView.measureWrapContent()

        val contentViewHeight = viewHeight - headerView.measuredHeight

        viewPager.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(contentViewHeight, MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        headerView.layoutToTopLeft(0, 0)

        val contentHeight = helloTextView.measuredHeight + nameTextView.measuredHeight
        val headerHeight = headerView.measuredHeight - getStatusBarHeight()
        helloTextView.layoutToTopLeft(
            convertDpToPx(20),
            getStatusBarHeight() + (headerHeight - contentHeight) / 2
        )

        nameTextView.layoutToTopLeft(
            helloTextView.left, helloTextView.bottom
        )

        accountImageView.layoutToTopRight(
            right - convertDpToPx(30),
            helloTextView.top + convertDpToPx(8)
        )

        accountBadgeTextView.layoutToTopLeft(
            accountImageView.right - accountImageView.measuredWidth * 3 / 10,
            accountImageView.top
        )
        viewPager.layoutToTopLeft(
            0,
            headerView.bottom
        )
    }
}