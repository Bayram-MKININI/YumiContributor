package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent

class VoucherItemView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var titleTextView: TextView
    private lateinit var expiryDateTextView: TextView
    private lateinit var descriptionTextView: TextView

    data class VoucherItemViewAdapter(
        val title: String = "",
        val expiryDate: String = "",
        val description: String = ""
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        titleTextView = findViewById(R.id.title_text_view)
        expiryDateTextView = findViewById(R.id.expiry_date_text_view)
        descriptionTextView = findViewById(R.id.description_text_view)
    }

    fun fillViewWithData(voucherItemViewAdapter: VoucherItemViewAdapter) {
        titleTextView.text = voucherItemViewAdapter.title
        expiryDateTextView.text = voucherItemViewAdapter.expiryDate
        descriptionTextView.text = voucherItemViewAdapter.description
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        titleTextView.measureWrapContent()

        expiryDateTextView.measureWrapContent()

        descriptionTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 9 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        viewHeight =
            titleTextView.measuredHeight + expiryDateTextView.measuredHeight + descriptionTextView.measuredHeight + convertDpToPx(
                40
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
            convertDpToPx(15),
            convertDpToPx(15)
        )

        expiryDateTextView.layoutToTopLeft(
            convertDpToPx(15),
            titleTextView.bottom + convertDpToPx(5)
        )

        descriptionTextView.layoutToTopLeft(
            convertDpToPx(15),
            expiryDateTextView.bottom + convertDpToPx(5)
        )
    }
}