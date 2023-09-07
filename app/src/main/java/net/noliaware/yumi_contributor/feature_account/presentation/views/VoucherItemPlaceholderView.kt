package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.views.ElevatedCardView
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight

class VoucherItemPlaceholderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ElevatedCardView(context, attrs, defStyle) {

    private lateinit var highlightView: View
    private lateinit var titleView: View
    private lateinit var retailerView: View

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        highlightView = findViewById(R.id.highlight_view)
        titleView = findViewById(R.id.title_view)
        retailerView = findViewById(R.id.retailer_view)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)

        highlightView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 1 / 2, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(30), MeasureSpec.EXACTLY)
        )

        titleView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 1 / 2, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(20), MeasureSpec.EXACTLY)
        )

        retailerView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 4 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(15), MeasureSpec.EXACTLY)
        )

        val viewHeight = highlightView.measuredHeight + titleView.measuredHeight + retailerView.measuredHeight +
                    convertDpToPx(35)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        highlightView.layoutToTopRight(viewWidth, 0)

        titleView.layoutToTopLeft(
            convertDpToPx(20),
            highlightView.bottom + convertDpToPx(10)
        )

        retailerView.layoutToTopLeft(
            titleView.left,
            titleView.bottom + convertDpToPx(10)
        )
    }
}