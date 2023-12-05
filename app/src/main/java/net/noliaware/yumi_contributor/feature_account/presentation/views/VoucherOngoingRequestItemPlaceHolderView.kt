package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft

class VoucherOngoingRequestItemPlaceHolderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var titleView: View
    private lateinit var dateView: View
    private lateinit var bodyView: View

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        titleView = findViewById(R.id.title_view)
        dateView = findViewById(R.id.date_view)
        bodyView = findViewById(R.id.body_view)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)

        titleView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 9 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(convertDpToPx(20), MeasureSpec.EXACTLY)
        )

        dateView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 6 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(convertDpToPx(15), MeasureSpec.EXACTLY)
        )

        bodyView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 9 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(convertDpToPx(40), MeasureSpec.EXACTLY)
        )

        val contentMeasuredHeight = titleView.measuredHeight + dateView.measuredHeight +
                bodyView.measuredHeight + convertDpToPx(40)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(contentMeasuredHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        titleView.layoutToTopLeft(
            viewWidth * 5 / 100,
            convertDpToPx(10)
        )

        dateView.layoutToTopLeft(
            titleView.left,
            titleView.bottom + convertDpToPx(10)
        )

        bodyView.layoutToTopLeft(
            titleView.left,
            dateView.bottom + convertDpToPx(10)
        )
    }
}