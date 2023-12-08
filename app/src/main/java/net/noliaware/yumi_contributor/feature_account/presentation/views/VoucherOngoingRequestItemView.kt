package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.sizeForVisible

class VoucherOngoingRequestItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var titleTextView: TextView
    private lateinit var deleteImageView: ImageView
    private lateinit var dateLabelTextView: TextView
    private lateinit var dateValueTextView: TextView
    private lateinit var bodyTextView: TextView
    var callback: VoucherOngoingRequestItemViewCallback? = null

    fun interface VoucherOngoingRequestItemViewCallback {
        fun onDeleteButtonClicked()
    }

    data class VoucherOngoingRequestItemViewAdapter(
        val title: String = "",
        val isDeletable: Boolean = false,
        val date: String = "",
        val body: String = ""
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        titleTextView = findViewById(R.id.title_text_view)
        deleteImageView = findViewById(R.id.delete_image_view)
        deleteImageView.setOnClickListener {
            callback?.onDeleteButtonClicked()
        }
        dateLabelTextView = findViewById(R.id.date_label_text_view)
        dateValueTextView = findViewById(R.id.date_value_text_view)
        bodyTextView = findViewById(R.id.body_text_view)
    }

    fun fillViewWithData(adapter: VoucherOngoingRequestItemViewAdapter) {
        titleTextView.text = adapter.title
        if (adapter.isDeletable) {
            deleteImageView.isVisible = true
        } else {
            deleteImageView.isGone = true
        }
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

        deleteImageView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(30), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val titleTextViewWidth = viewWidth - convertDpToPx(20) -
                deleteImageView.sizeForVisible { deleteImageView.measuredWidth + convertDpToPx(10) }
        titleTextView.measure(
            MeasureSpec.makeMeasureSpec(titleTextViewWidth, MeasureSpec.AT_MOST),
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
            convertDpToPx(10),
            convertDpToPx(10)
        )

        deleteImageView.layoutToTopRight(
            viewWidth - convertDpToPx(10),
            titleTextView.top
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