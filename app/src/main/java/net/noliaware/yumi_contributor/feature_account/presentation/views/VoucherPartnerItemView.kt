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
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight
import net.noliaware.yumi_contributor.commun.util.sizeForVisible
import kotlin.math.max

class VoucherPartnerItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var partnerTextView: TextView
    private lateinit var informationTextView: TextView
    var callback: VoucherPartnerItemViewCallback? = null

    data class VoucherPartnerItemViewAdapter(
        val partnerLabel: String? = null,
        val linkAvailable: Boolean = false,
    )

    fun interface VoucherPartnerItemViewCallback {
        fun onPartnerInfoClicked()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        partnerTextView = findViewById(R.id.partner_text_view)
        informationTextView = findViewById(R.id.information_text_view)
        informationTextView.setOnClickListener {
            callback?.onPartnerInfoClicked()
        }
    }

    fun fillViewWithData(adapter: VoucherPartnerItemViewAdapter) {
        partnerTextView.text = adapter.partnerLabel
        if (adapter.linkAvailable) {
            informationTextView.isVisible = true
        } else {
            informationTextView.isGone = true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)

        if (informationTextView.isVisible) {
            informationTextView.measure(
                MeasureSpec.makeMeasureSpec(convertDpToPx(20), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(convertDpToPx(20), MeasureSpec.EXACTLY)
            )
        }

        val partnerTextViewWidth = viewWidth - (informationTextView.sizeForVisible {
            informationTextView.measuredWidth + convertDpToPx(10)
        } + convertDpToPx(30))
        partnerTextView.measure(
            MeasureSpec.makeMeasureSpec(partnerTextViewWidth, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val viewHeight = max(
            partnerTextView.measuredHeight + convertDpToPx(20),
            convertDpToPx(40)
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        partnerTextView.layoutToTopLeft(
            convertDpToPx(15),
            convertDpToPx(10)
        )
        if (informationTextView.isVisible) {
            informationTextView.layoutToTopRight(
                viewWidth - convertDpToPx(15),
                partnerTextView.top
            )
        }
    }
}