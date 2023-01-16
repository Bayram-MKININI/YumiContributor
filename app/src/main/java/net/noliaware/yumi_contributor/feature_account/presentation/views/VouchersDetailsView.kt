package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.views.DataValueView
import net.noliaware.yumi_contributor.commun.util.*

class VouchersDetailsView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var backView: View
    private lateinit var parentContentView: View
    private lateinit var contentView: LinearLayoutCompat
    private lateinit var titleTextView: TextView
    private lateinit var partnerDescriptionTextView: TextView
    private lateinit var displayVoucherTextView: TextView
    private lateinit var voucherStatusTextView: TextView

    var callback: VouchersDetailsViewCallback? by weak()

    data class VouchersDetailsViewAdapter(
        val title: String = "",
        val partnerDescription: String? = null,
        val partnerURL: String? = null,
        val hideDisplayVoucher: Boolean = false,
    )

    interface VouchersDetailsViewCallback {
        fun onBackButtonClicked()
        fun onPartnerInfoClicked()
        fun onLocationClicked()
        fun onDisplayVoucherButtonClicked()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {

        backView = findViewById(R.id.back_view)
        backView.setOnClickListener(onButtonClickListener)

        parentContentView = findViewById(R.id.parent_content_layout)
        contentView = parentContentView.findViewById(R.id.content_layout)
        titleTextView = contentView.findViewById(R.id.title_text_view)
        partnerDescriptionTextView = contentView.findViewById(R.id.partner_description_text_view)
        partnerDescriptionTextView.setOnClickListener {
            callback?.onPartnerInfoClicked()
        }

        displayVoucherTextView = findViewById(R.id.display_voucher_text_view)
        displayVoucherTextView.setOnClickListener(onButtonClickListener)

        voucherStatusTextView = findViewById(R.id.voucher_status_text_view)
    }

    private val onButtonClickListener: OnClickListener by lazy {
        OnClickListener {
            when (it.id) {
                R.id.back_view -> callback?.onBackButtonClicked()
                R.id.display_voucher_text_view -> callback?.onDisplayVoucherButtonClicked()
            }
        }
    }

    fun fillViewWithData(vouchersDetailsViewAdapter: VouchersDetailsViewAdapter) {

        titleTextView.text = vouchersDetailsViewAdapter.title

        vouchersDetailsViewAdapter.partnerURL?.let {
            partnerDescriptionTextView.text = vouchersDetailsViewAdapter.partnerDescription
        } ?: run {
            partnerDescriptionTextView.isGone = true
        }

        partnerDescriptionTextView.text = vouchersDetailsViewAdapter.partnerDescription

        displayVoucherTextView.isGone = vouchersDetailsViewAdapter.hideDisplayVoucher
    }

    fun addDataValue(dataValueViewAdapter: DataValueView.DataValueViewAdapter) {
        DataValueView(context).also {
            //layoutParams = LayoutParams(measuredWidth * 9 / 10, LayoutParams.WRAP_CONTENT)
            contentView.addView(it)
            it.fillViewWithData(dataValueViewAdapter)
        }
    }

    fun addLocationView(onLocationClicked: () -> Unit) {
        LocationView(context).apply {
            locationClickedAction = onLocationClicked
        }.also {
            contentView.addView(it)
        }
    }

    fun setVoucherStatus(voucherStatus: String) {
        voucherStatusTextView.isVisible = true
        displayVoucherTextView.isGone = true
        voucherStatusTextView.text = voucherStatus
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        backView.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        if (displayVoucherTextView.isVisible) {
            displayVoucherTextView.measure(
                MeasureSpec.makeMeasureSpec(viewWidth * 7 / 10, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(convertDpToPx(40), MeasureSpec.EXACTLY)
            )
        }

        if (voucherStatusTextView.isVisible) {
            voucherStatusTextView.measureWrapContent()
        }

        val parentContentViewHeight =
            viewHeight - (backView.measuredHeight + displayVoucherTextView.measuredHeight + convertDpToPx(
                100
            ))

        parentContentView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(parentContentViewHeight, MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        val viewWidth = right - left
        val viewHeight = bottom - top

        backView.layoutToTopLeft(convertDpToPx(10), getStatusBarHeight() + convertDpToPx(10))

        parentContentView.layoutToTopLeft(
            0,
            backView.bottom + convertDpToPx(20)
        )


        when {
            voucherStatusTextView.isVisible -> {
                voucherStatusTextView.layoutToBottomLeft(
                    (viewWidth - voucherStatusTextView.measuredWidth) / 2,
                    bottom - convertDpToPx(40)
                )
            }
            displayVoucherTextView.isVisible -> {
                displayVoucherTextView.layoutToBottomLeft(
                    (viewWidth - displayVoucherTextView.measuredWidth) / 2,
                    bottom - convertDpToPx(40)
                )
            }
        }
    }
}