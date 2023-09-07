package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.views.ElevatedCardView
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight

class CategoryItemPlaceholderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ElevatedCardView(context, attrs, defStyle) {

    private lateinit var countView: View
    private lateinit var iconView: View
    private lateinit var titleView: View

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        countView = findViewById(R.id.count_view)
        iconView = findViewById(R.id.icon_view)
        titleView = findViewById(R.id.title_view)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        countView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(30), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(30), MeasureSpec.EXACTLY)
        )

        iconView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 5 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewWidth * 5 / 10, MeasureSpec.EXACTLY)
        )

        titleView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(80), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(15), MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        countView.layoutToTopRight(
            viewWidth - convertDpToPx(10),
            convertDpToPx(10)
        )

        iconView.layoutToTopLeft(
            (viewWidth - iconView.measuredWidth) / 2,
            (viewHeight - iconView.measuredHeight) / 2
        )

        titleView.layoutToTopLeft(
            (viewWidth - titleView.measuredWidth) / 2,
            iconView.bottom + convertDpToPx(5)
        )
    }
}