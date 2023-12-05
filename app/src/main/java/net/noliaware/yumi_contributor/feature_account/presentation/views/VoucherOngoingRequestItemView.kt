package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.sizeForVisible

class VoucherOngoingRequestItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var titleTextView: TextView
    private lateinit var dateLabelTextView: TextView
    private lateinit var dateValueTextView: TextView
    private lateinit var bodyTextView: TextView

    data class VoucherOngoingRequestItemViewAdapter(
        val title: String = "",
        val date: String = "",
        val body: String = ""
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        titleTextView = findViewById(R.id.title_text_view)
        dateLabelTextView = findViewById(R.id.date_label_text_view)
        dateValueTextView = findViewById(R.id.date_value_text_view)
        bodyTextView = findViewById(R.id.body_text_view)
    }

    fun fillViewWithData(adapter: VoucherOngoingRequestItemViewAdapter) {
        titleTextView.text = adapter.title
        dateValueTextView.text = adapter.date
        if (adapter.body.isNotEmpty()) {
            bodyTextView.isVisible = true
            bodyTextView.text = adapter.body
        } else {
            bodyTextView.isGone = true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)

        titleTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 9 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        dateLabelTextView.measureWrapContent()
        dateValueTextView.measureWrapContent()

        bodyTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 9 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val contentMeasuredHeight = titleTextView.measuredHeight + dateLabelTextView.measuredHeight +
                    bodyTextView.sizeForVisible {
                        bodyTextView.measuredHeight + convertDpToPx(10)
                    } + convertDpToPx(30)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(contentMeasuredHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        titleTextView.layoutToTopLeft(
            viewWidth * 5 / 100,
            convertDpToPx(10)
        )

        dateLabelTextView.layoutToTopLeft(
            titleTextView.left,
            titleTextView.bottom + convertDpToPx(10)
        )

        dateValueTextView.layoutToTopLeft(
            dateLabelTextView.right + convertDpToPx(5),
            dateLabelTextView.top + (dateLabelTextView.measuredHeight - dateValueTextView.measuredHeight) / 2
        )

        bodyTextView.layoutToTopLeft(
            titleTextView.left,
            dateLabelTextView.bottom + convertDpToPx(10)
        )
    }
}