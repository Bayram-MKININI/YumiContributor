package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToBottomLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import kotlin.math.max

class AccountItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var titleTextView: TextView
    private lateinit var countTextView: TextView
    private lateinit var phoneImageView: ImageView
    private lateinit var separatorView: View
    var callback: AccountsListItemViewCallback? = null

    fun interface AccountsListItemViewCallback {
        fun onPhoneButtonClicked()
    }

    data class AccountItemViewAdapter(
        val title: String = "",
        val count: String = "",
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        titleTextView = findViewById(R.id.title_text_view)
        countTextView = findViewById(R.id.count_text_view)
        phoneImageView = findViewById(R.id.phone_image_view)
        separatorView = findViewById(R.id.separator_line_view)
        phoneImageView.setOnClickListener {
            callback?.onPhoneButtonClicked()
        }
    }

    fun fillViewWithData(accountItemViewAdapter: AccountItemViewAdapter) {
        titleTextView.text = accountItemViewAdapter.title
        countTextView.text = accountItemViewAdapter.count
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)

        phoneImageView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(30), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(30), MeasureSpec.EXACTLY)
        )

        countTextView.measureWrapContent()

        val titleTextViewWidth = viewWidth - (phoneImageView.measuredWidth + countTextView.measuredWidth +
                    convertDpToPx(70))
        titleTextView.measure(
            MeasureSpec.makeMeasureSpec(titleTextViewWidth, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        separatorView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth - convertDpToPx(30), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(1), MeasureSpec.EXACTLY)
        )

        val viewHeight = max(titleTextView.measuredHeight, phoneImageView.measuredHeight) +
                convertDpToPx(20)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        phoneImageView.layoutToTopRight(
            viewWidth - convertDpToPx(20),
            (viewHeight - phoneImageView.measuredHeight) / 2
        )

        countTextView.layoutToTopRight(
            phoneImageView.left - convertDpToPx(20),
            phoneImageView.top + (phoneImageView.measuredHeight - countTextView.measuredHeight) / 2
        )

        titleTextView.layoutToTopLeft(
            convertDpToPx(20),
            phoneImageView.top + (phoneImageView.measuredHeight - titleTextView.measuredHeight) / 2
        )

        separatorView.layoutToBottomLeft(
            (viewWidth - separatorView.measuredWidth) / 2,
            viewHeight
        )
    }
}