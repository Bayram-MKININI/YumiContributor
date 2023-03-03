package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.views.ElevatedCardView
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.tint

class VoucherItemView(context: Context, attrs: AttributeSet?) : ElevatedCardView(context, attrs) {

    private lateinit var highlightLayout: LinearLayoutCompat
    private lateinit var highlightDescriptionTextView: TextView
    private lateinit var highlightValueTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var retrieveTextView: TextView
    private lateinit var retailerTextView: TextView

    data class VoucherItemViewAdapter(
        val color: Int,
        val highlightDescription: String = "",
        val highlightValue: String = "",
        val title: String = "",
        val retailer: String = ""
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        highlightLayout = findViewById(R.id.highlight_layout)
        highlightDescriptionTextView = highlightLayout.findViewById(R.id.highlight_description_text_view)
        highlightValueTextView = highlightLayout.findViewById(R.id.highlight_value_text_view)
        titleTextView = findViewById(R.id.title_text_view)
        retrieveTextView = findViewById(R.id.retrieve_text_view)
        retailerTextView = findViewById(R.id.retailer_text_view)
    }

    fun fillViewWithData(voucherItemViewAdapter: VoucherItemViewAdapter) {
        highlightDescriptionTextView.text = voucherItemViewAdapter.highlightDescription
        highlightValueTextView.text = voucherItemViewAdapter.highlightValue
        highlightLayout.background = ContextCompat.getDrawable(
            context,
            R.drawable.rectangle_rounded_15dp
        )?.tint(voucherItemViewAdapter.color)
        titleTextView.text = voucherItemViewAdapter.title
        retailerTextView.text = voucherItemViewAdapter.retailer
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        titleTextView.measureWrapContent()

        highlightLayout.measureWrapContent()

        retrieveTextView.measureWrapContent()

        val retailerTextViewWidth = viewWidth - (retrieveTextView.measuredWidth + convertDpToPx(42))
        retailerTextView.measure(
            MeasureSpec.makeMeasureSpec(retailerTextViewWidth, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        viewHeight = highlightLayout.measuredHeight + titleTextView.measuredHeight + retrieveTextView.measuredHeight +
                convertDpToPx(45)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        highlightLayout.layoutToTopLeft(
            (viewWidth - highlightLayout.measuredWidth) / 2,
            convertDpToPx(10)
        )

        titleTextView.layoutToTopLeft(
            convertDpToPx(20),
            highlightLayout.bottom + convertDpToPx(10)
        )

        retrieveTextView.layoutToTopLeft(
            titleTextView.left,
            titleTextView.bottom + convertDpToPx(5)
        )

        retailerTextView.layoutToTopLeft(
            retrieveTextView.right + convertDpToPx(2),
            retrieveTextView.top
        )
    }
}