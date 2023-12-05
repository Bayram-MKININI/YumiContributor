package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.text.SpannableString
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.views.ElevatedCardView
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.getDrawableCompat
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.tint

class VoucherItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ElevatedCardView(context, attrs, defStyle) {

    private lateinit var highlightLayout: View
    private lateinit var highlightTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var ongoingRequestsImageView: ImageView
    private lateinit var retrieveTextView: TextView
    private lateinit var retailerTextView: TextView

    data class VoucherItemViewAdapter(
        val isDeactivated: Boolean = false,
        val color: Int,
        val highlight: SpannableString,
        val title: String = "",
        val hasOngoingRequests: Boolean = false,
        val retailerDescription: String = "",
        val retailerValue: String? = ""
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        highlightLayout = findViewById(R.id.highlight_layout)
        highlightTextView = highlightLayout.findViewById(R.id.highlight_text_view)
        ongoingRequestsImageView = highlightLayout.findViewById(R.id.ongoing_requests_image_view)
        titleTextView = findViewById(R.id.title_text_view)
        retrieveTextView = findViewById(R.id.retrieve_text_view)
        retailerTextView = findViewById(R.id.retailer_text_view)
    }

    fun fillViewWithData(voucherItemViewAdapter: VoucherItemViewAdapter) {
        alpha = if (voucherItemViewAdapter.isDeactivated) {
            0.4f
        } else {
            1f
        }
        highlightTextView.text = voucherItemViewAdapter.highlight
        highlightLayout.background = context.getDrawableCompat(
            R.drawable.rectangle_rounded_15dp
        )?.tint(voucherItemViewAdapter.color)
        if (voucherItemViewAdapter.hasOngoingRequests) {
            ongoingRequestsImageView.isVisible = true
        } else {
            ongoingRequestsImageView.isGone = true
        }
        titleTextView.text = voucherItemViewAdapter.title
        retrieveTextView.text = voucherItemViewAdapter.retailerDescription
        retailerTextView.text = voucherItemViewAdapter.retailerValue
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)

        highlightLayout.measureWrapContent()

        titleTextView.measureWrapContent()

        retrieveTextView.measureWrapContent()

        val retailerTextViewWidth = viewWidth - (retrieveTextView.measuredWidth + convertDpToPx(44))
        retailerTextView.measure(
            MeasureSpec.makeMeasureSpec(retailerTextViewWidth, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val viewHeight = highlightLayout.measuredHeight + titleTextView.measuredHeight + retrieveTextView.measuredHeight +
                    convertDpToPx(30)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        highlightLayout.layoutToTopRight(viewWidth, 0)

        titleTextView.layoutToTopLeft(
            convertDpToPx(20),
            highlightLayout.bottom + convertDpToPx(10)
        )

        retrieveTextView.layoutToTopLeft(
            titleTextView.left,
            titleTextView.bottom + convertDpToPx(5)
        )

        retailerTextView.layoutToTopLeft(
            retrieveTextView.right + convertDpToPx(4),
            retrieveTextView.top
        )
    }
}