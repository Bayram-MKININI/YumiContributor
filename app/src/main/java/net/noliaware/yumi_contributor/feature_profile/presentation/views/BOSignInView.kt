package net.noliaware.yumi_contributor.feature_profile.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.views.ElevatedCardView
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent

class BOSignInView(context: Context, attrs: AttributeSet?) : ElevatedCardView(context, attrs) {

    private lateinit var descriptionTextView: TextView
    private lateinit var codeTextView: TextView
    private lateinit var boCodeExpiration: TextView
    private lateinit var timestampTextView: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {

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

        descriptionTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 8 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        codeTextView.measureWrapContent()
        boCodeExpiration.measureWrapContent()
        timestampTextView.measureWrapContent()

        val contentViewHeight = descriptionTextView.measuredHeight + codeTextView.measuredHeight + boCodeExpiration.measuredHeight +
                timestampTextView.measuredHeight + convertDpToPx(140)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(contentViewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        val viewWidth = right - left
        val viewHeight = bottom - top

        descriptionTextView.layoutToTopLeft(
            (viewWidth - descriptionTextView.measuredWidth) / 2,
            convertDpToPx(40)
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