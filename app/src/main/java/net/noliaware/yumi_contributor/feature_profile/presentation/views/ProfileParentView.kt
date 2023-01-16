package net.noliaware.yumi_contributor.feature_profile.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.*

class ProfileParentView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var titleTextView: TextView
    private lateinit var parentContentView: View
    private lateinit var contentView: ProfileView

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        titleTextView = findViewById(R.id.title_text_view)
        parentContentView = findViewById(R.id.parent_content_view)
        contentView = findViewById(R.id.profile_view)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        titleTextView.measureWrapContent()

        val parentContentViewHeight = viewHeight - (titleTextView.measuredHeight + getStatusBarHeight() + convertDpToPx(30))

        parentContentView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(parentContentViewHeight, MeasureSpec.EXACTLY)
        )

        if (contentView.measuredHeight < parentContentView.measuredHeight)
            contentView.measure(
                MeasureSpec.makeMeasureSpec(contentView.measuredWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(parentContentView.measuredHeight, MeasureSpec.EXACTLY)
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

        parentContentView.layoutToBottomLeft(
            0,
            viewHeight
        )
    }
}