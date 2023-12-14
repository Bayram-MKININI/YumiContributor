package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.graphics.Bitmap
import android.text.SpannableString
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.UI.GOLDEN_RATIO
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.drawableIdByName
import net.noliaware.yumi_contributor.commun.util.getStatusBarHeight
import net.noliaware.yumi_contributor.commun.util.layoutToBottomLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import kotlin.math.min
import kotlin.math.roundToInt

class QrCodeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var backgroundView: View
    private lateinit var headerView: View
    private lateinit var backView: View
    private lateinit var categoryImageView: ImageView
    private lateinit var contentView: View
    private lateinit var titleTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var qrCodeBackgroundView: View

    private lateinit var qrCodeImageView: ImageView
    private lateinit var layerView: View
    private lateinit var useVoucherLayout: LinearLayoutCompat
    var callback: QrCodeViewCallback? = null

    data class QrCodeViewAdapter(
        val color: Int,
        val iconName: String?,
        val title: String,
        val date: SpannableString
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
        backgroundView = findViewById(R.id.background_view)
        headerView = findViewById(R.id.header_view)
        backView = findViewById(R.id.back_view)
        backView.setOnClickListener(onButtonClickListener)
        categoryImageView = findViewById(R.id.category_image_view)
        contentView = findViewById(R.id.content_layout)

        titleTextView = contentView.findViewById(R.id.title_text_view)
        dateTextView = contentView.findViewById(R.id.date_text_view)
        qrCodeBackgroundView = contentView.findViewById(R.id.qr_code_background)
        qrCodeImageView = contentView.findViewById(R.id.qr_code_image_view)
        layerView = contentView.findViewById(R.id.layer_view)
        useVoucherLayout = contentView.findViewById(R.id.use_voucher_layout)
        useVoucherLayout.setOnClickListener(onButtonClickListener)
    }

    private val onButtonClickListener: OnClickListener by lazy {
        OnClickListener {
            when (it.id) {
                R.id.back_view -> callback?.onBackButtonClicked()
                R.id.use_voucher_layout -> callback?.onUseVoucherButtonClicked()
            }
        }
    }

    fun fillViewWithData(qrCodeViewAdapter: QrCodeViewAdapter) {
        headerView.setBackgroundColor(qrCodeViewAdapter.color)
        categoryImageView.setImageResource(context.drawableIdByName(qrCodeViewAdapter.iconName))
        titleTextView.text = qrCodeViewAdapter.title
        dateTextView.text = qrCodeViewAdapter.date
    }

    fun setQrCode(bitmap: Bitmap) {
        qrCodeImageView.setImageBitmap(bitmap)
    }

    fun revealQrCode() {
        layerView.isGone = true
        qrCodeImageView.alpha = 1f
    }

    fun isQrCodeRevealed() = layerView.isGone

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        backgroundView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight - getStatusBarHeight(), MeasureSpec.EXACTLY)
        )

        headerView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(
                getStatusBarHeight() + convertDpToPx(92),
                MeasureSpec.EXACTLY
            )
        )

        backView.measureWrapContent()

        categoryImageView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(86), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(86), MeasureSpec.EXACTLY)
        )

        val parentContentViewHeight = viewHeight - (headerView.measuredHeight + categoryImageView.measuredHeight / 2 +
                convertDpToPx(25))

        contentView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 95 / 100, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(parentContentViewHeight, MeasureSpec.EXACTLY)
        )

        titleTextView.measureWrapContent()
        dateTextView.measureWrapContent()
        useVoucherLayout.measureWrapContent()

        val availableViewHeight = contentView.measuredHeight - (titleTextView.measuredHeight + dateTextView.measuredHeight +
                useVoucherLayout.measuredHeight + convertDpToPx(90))

        val qrCodeBackgroundViewWidth = min(availableViewHeight, contentView.measuredWidth * 9 / 10)
        qrCodeBackgroundView.measure(
            MeasureSpec.makeMeasureSpec(qrCodeBackgroundViewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(qrCodeBackgroundViewWidth, MeasureSpec.EXACTLY)
        )

        val qrCodeImageViewSize = (qrCodeBackgroundView.measuredWidth / GOLDEN_RATIO).roundToInt()

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

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        backgroundView.layoutToTopLeft(0, getStatusBarHeight())

        headerView.layoutToTopLeft(0, 0)

        backView.layoutToTopLeft(convertDpToPx(10), getStatusBarHeight() + convertDpToPx(10))

        categoryImageView.layoutToTopLeft(
            (viewWidth - categoryImageView.measuredWidth) / 2,
            headerView.bottom - categoryImageView.measuredHeight / 2
        )

        contentView.layoutToTopLeft(
            (viewWidth - contentView.measuredWidth) / 2,
            categoryImageView.bottom + convertDpToPx(15)
        )

        titleTextView.layoutToTopLeft(
            convertDpToPx(20),
            convertDpToPx(20)
        )

        dateTextView.layoutToTopLeft(
            titleTextView.left,
            titleTextView.bottom + convertDpToPx(5)
        )

        useVoucherLayout.layoutToBottomLeft(
            (contentView.measuredWidth - useVoucherLayout.measuredWidth) / 2,
            contentView.measuredHeight - convertDpToPx(40)
        )

        val availableSpaceForQrCode = useVoucherLayout.top - dateTextView.bottom
        qrCodeBackgroundView.layoutToTopLeft(
            (contentView.measuredWidth - qrCodeBackgroundView.measuredWidth) / 2,
            dateTextView.bottom + (availableSpaceForQrCode - qrCodeBackgroundView.measuredHeight) / 2
        )

        qrCodeImageView.layoutToTopLeft(
            qrCodeBackgroundView.left + (qrCodeBackgroundView.measuredWidth - qrCodeImageView.measuredWidth) / 2,
            qrCodeBackgroundView.top + (qrCodeBackgroundView.measuredHeight - qrCodeImageView.measuredHeight) / 2
        )

        if (layerView.isVisible) {
            layerView.layoutToTopLeft(
                qrCodeImageView.left,
                qrCodeImageView.top
            )
        }
    }
}