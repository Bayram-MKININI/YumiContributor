package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToBottomLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight
import kotlin.math.max

class AccountItemPlaceholderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var titleView: View
    private lateinit var countView: View
    private lateinit var phoneView: View
    private lateinit var separatorView: View

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        titleView = findViewById(R.id.title_view)
        countView = findViewById(R.id.count_view)
        phoneView = findViewById(R.id.phone_view)
        separatorView = findViewById(R.id.separator_line_view)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)

        phoneView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(30), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(30), MeasureSpec.EXACTLY)
        )

        countView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(30), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(30), MeasureSpec.EXACTLY)
        )

        titleView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 1 / 2, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(15), MeasureSpec.EXACTLY)
        )

        separatorView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth - convertDpToPx(30), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(1), MeasureSpec.EXACTLY)
        )

        val viewHeight = max(titleView.measuredHeight, phoneView.measuredHeight) + convertDpToPx(20)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        phoneView.layoutToTopRight(
            viewWidth - convertDpToPx(20),
            (viewHeight - phoneView.measuredHeight) / 2
        )

        countView.layoutToTopRight(
            phoneView.left - convertDpToPx(20),
            phoneView.top + (phoneView.measuredHeight - countView.measuredHeight) / 2
        )

        titleView.layoutToTopLeft(
            convertDpToPx(20),
            phoneView.top + (phoneView.measuredHeight - titleView.measuredHeight) / 2
        )

        separatorView.layoutToBottomLeft(
            (viewWidth - separatorView.measuredWidth) / 2,
            viewHeight
        )
    }
}