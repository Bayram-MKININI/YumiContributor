package net.noliaware.yumi_contributor.feature_profile.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.GOLDEN_RATIO
import net.noliaware.yumi_contributor.commun.util.*

class BOSignInView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var backView: View
    private lateinit var descriptionTextView: TextView
    private lateinit var codeTextView: TextView
    private lateinit var boCodeExpiration: TextView
    private lateinit var timestampTextView: TextView

    var callback: BOSignInViewCallback? by weak()

    interface BOSignInViewCallback {
        fun onBackButtonClicked()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {

        backView = findViewById(R.id.back_view)
        backView.setOnClickListener {
            callback?.onBackButtonClicked()
        }

        descriptionTextView = findViewById(R.id.description_text_view)
        codeTextView = findViewById(R.id.code_text_view)
        boCodeExpiration = findViewById(R.id.bo_code_expiration)
        timestampTextView = findViewById(R.id.timestamp_text_view)
    }

    fun displayCode(code: String) {
        codeTextView.text = code
    }

    fun displayRemainingTime(remainingTime: String) {
        timestampTextView.text = remainingTime
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        backView.measureWrapContent()

        descriptionTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth - convertDpToPx(40), MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        codeTextView.measureWrapContent()

        boCodeExpiration.measureWrapContent()

        timestampTextView.measureWrapContent()

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        val viewWidth = right - left
        val viewHeight = bottom - top

        backView.layoutToTopLeft(
            convertDpToPx(10),
            getStatusBarHeight() + convertDpToPx(10)
        )

        val contentHeight = descriptionTextView.measuredHeight + codeTextView.measuredHeight +
                boCodeExpiration.measuredHeight + timestampTextView.measuredHeight + convertDpToPx(60)

        val descriptionTextViewTop =
            ((viewHeight * (1 - 1 / GOLDEN_RATIO)) - contentHeight / 2).toInt()

        descriptionTextView.layoutToTopLeft(
            (viewWidth - descriptionTextView.measuredWidth) / 2,
            descriptionTextViewTop
        )

        codeTextView.layoutToTopLeft(
            (viewWidth - codeTextView.measuredWidth) / 2,
            descriptionTextView.bottom + convertDpToPx(20)
        )

        boCodeExpiration.layoutToTopLeft(
            (viewWidth - boCodeExpiration.measuredWidth) / 2,
            codeTextView.bottom + convertDpToPx(20)
        )

        timestampTextView.layoutToTopLeft(
            (viewWidth - timestampTextView.measuredWidth) / 2,
            boCodeExpiration.bottom + convertDpToPx(20)
        )
    }
}