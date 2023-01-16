package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.GOLDEN_RATIO
import net.noliaware.yumi_contributor.commun.util.*
import kotlin.math.roundToInt

class QrCodeView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var backView: View
    private lateinit var titleTextView: TextView
    private lateinit var creationDateTitleTextView: TextView
    private lateinit var creationDateValueTextView: TextView
    private lateinit var expiryDateTitleTextView: TextView
    private lateinit var expiryDateValueTextView: TextView
    private lateinit var qrCodeImageView: ImageView
    private lateinit var layerView: View
    private lateinit var useVoucherTextView: TextView

    var callback: QrCodeViewCallback? by weak()

    data class QrCodeViewAdapter(
        val title: String,
        val creationDate: String,
        val expiryDate: String
    )

    interface QrCodeViewCallback {
        fun onBackButtonClicked()
        fun onUseVoucherButtonClicked()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        backView = findViewById(R.id.back_view)
        backView.setOnClickListener(onButtonClickListener)
        titleTextView = findViewById(R.id.title_text_view)
        creationDateTitleTextView = findViewById(R.id.creation_date_title_text_view)
        creationDateValueTextView = findViewById(R.id.creation_date_value_text_view)
        expiryDateTitleTextView = findViewById(R.id.expiry_date_title_text_view)
        expiryDateValueTextView = findViewById(R.id.expiry_date_value_text_view)
        qrCodeImageView = findViewById(R.id.qr_code_image_view)
        layerView = findViewById(R.id.layer_view)
        useVoucherTextView = findViewById(R.id.use_vouchers_text_view)
        useVoucherTextView.setOnClickListener(onButtonClickListener)
    }

    private val onButtonClickListener: OnClickListener by lazy {
        OnClickListener {
            when (it.id) {
                R.id.back_view -> callback?.onBackButtonClicked()
                R.id.use_vouchers_text_view -> callback?.onUseVoucherButtonClicked()
            }
        }
    }

    fun fillViewWithData(qrCodeViewAdapter: QrCodeViewAdapter) {
        titleTextView.text = qrCodeViewAdapter.title
        creationDateValueTextView.text = qrCodeViewAdapter.creationDate
        expiryDateValueTextView.text = qrCodeViewAdapter.expiryDate
    }

    fun setQrCode(bitmap: Bitmap) {
        qrCodeImageView.setImageBitmap(bitmap)
    }

    fun revealQrCode() {
        layerView.isGone = true
    }

    fun isQrCodeRevealed() = layerView.isGone

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        backView.measureWrapContent()

        titleTextView.measureWrapContent()

        creationDateTitleTextView.measureWrapContent()

        creationDateValueTextView.measureWrapContent()

        expiryDateTitleTextView.measureWrapContent()

        expiryDateValueTextView.measureWrapContent()

        val qrCodeImageViewSize = (viewWidth / GOLDEN_RATIO).roundToInt()

        qrCodeImageView.measure(
            MeasureSpec.makeMeasureSpec(qrCodeImageViewSize, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(qrCodeImageViewSize, MeasureSpec.EXACTLY)
        )

        if (layerView.isVisible) {
            layerView.measure(
                MeasureSpec.makeMeasureSpec(qrCodeImageViewSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(qrCodeImageViewSize, MeasureSpec.EXACTLY)
            )
        }

        useVoucherTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 7 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(40), MeasureSpec.EXACTLY)
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

        val contentHeight =
            titleTextView.measuredHeight + creationDateTitleTextView.measuredHeight + expiryDateTitleTextView.measuredHeight + qrCodeImageView.measuredHeight + convertDpToPx(
                45
            )
        titleTextView.layoutToTopLeft(
            (viewWidth - titleTextView.measuredWidth) / 2,
            (viewHeight - contentHeight) / 2
        )

        creationDateTitleTextView.layoutToTopRight(
            viewWidth / 2 - convertDpToPx(5),
            titleTextView.bottom + convertDpToPx(15)
        )

        creationDateValueTextView.layoutToTopLeft(
            viewWidth / 2 + convertDpToPx(5),
            creationDateTitleTextView.top + (creationDateTitleTextView.measuredHeight - creationDateValueTextView.measuredHeight) / 2
        )

        expiryDateTitleTextView.layoutToTopRight(
            viewWidth / 2 - convertDpToPx(5),
            creationDateValueTextView.bottom + convertDpToPx(10)
        )

        expiryDateValueTextView.layoutToTopLeft(
            viewWidth / 2 + convertDpToPx(5),
            expiryDateTitleTextView.top + (expiryDateTitleTextView.measuredHeight - expiryDateValueTextView.measuredHeight) / 2
        )

        qrCodeImageView.layoutToTopLeft(
            (viewWidth - qrCodeImageView.measuredWidth) / 2,
            expiryDateTitleTextView.bottom + convertDpToPx(20)
        )

        if (layerView.isVisible) {
            layerView.layoutToTopLeft(
                qrCodeImageView.left,
                qrCodeImageView.top
            )
        }

        useVoucherTextView.layoutToBottomLeft(
            (viewWidth - useVoucherTextView.measuredWidth) / 2,
            bottom - convertDpToPx(40)
        )
    }
}