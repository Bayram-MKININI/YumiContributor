package net.noliaware.yumi_contributor.feature_profile.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.*

class BOSignInView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var headerView: View
    private lateinit var profileIconView: View
    private lateinit var backView: View
    private lateinit var backgroundView: ImageView
    private lateinit var contentView: View
    private lateinit var descriptionTextView: TextView
    private lateinit var codeTextView: TextView
    private lateinit var boCodeExpiration: TextView
    private lateinit var timestampTextView: TextView

    var callback: BOSignInViewCallback? by weak()

    fun interface BOSignInViewCallback {
        fun onBackButtonClicked()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {

        headerView = findViewById(R.id.header_view)
        profileIconView = findViewById(R.id.profile_icon_view)
        backView = findViewById(R.id.back_view)
        backView.setOnClickListener { callback?.onBackButtonClicked() }
        backgroundView = findViewById(R.id.background_view)

        contentView = findViewById(R.id.content_layout)
        descriptionTextView = contentView.findViewById(R.id.description_text_view)
        codeTextView = contentView.findViewById(R.id.code_text_view)
        boCodeExpiration = contentView.findViewById(R.id.bo_code_expiration)
        timestampTextView = contentView.findViewById(R.id.timestamp_text_view)
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

        headerView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(getStatusBarHeight() + convertDpToPx(75), MeasureSpec.EXACTLY)
        )

        backView.measureWrapContent()

        profileIconView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(50), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(50), MeasureSpec.EXACTLY)
        )

        backgroundView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight - getStatusBarHeight(), MeasureSpec.EXACTLY)
        )

        val contentViewWidth = viewWidth * 9 / 10
        descriptionTextView.measure(
            MeasureSpec.makeMeasureSpec(contentViewWidth * 8 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        codeTextView.measureWrapContent()

        boCodeExpiration.measureWrapContent()

        timestampTextView.measureWrapContent()

        val contentViewHeight = descriptionTextView.measuredHeight + codeTextView.measuredHeight + boCodeExpiration.measuredHeight +
                timestampTextView.measuredHeight + convertDpToPx(140)

        contentView.measure(
            MeasureSpec.makeMeasureSpec(contentViewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(contentViewHeight, MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        val viewWidth = right - left
        val viewHeight = bottom - top

        backgroundView.layoutToBottomLeft(
            0,
            viewHeight
        )

        headerView.layoutToTopLeft(0, 0)

        backView.layoutToTopLeft(
            convertDpToPx(10),
            getStatusBarHeight() + convertDpToPx(10)
        )

        profileIconView.layoutToTopLeft(
            (viewWidth - profileIconView.measuredWidth) / 2,
            headerView.bottom - profileIconView.measuredHeight / 2
        )

        contentView.layoutToTopLeft(
            (viewWidth - contentView.measuredWidth) / 2,
            (viewHeight - contentView.measuredHeight) / 2
        )

        descriptionTextView.layoutToTopLeft(
            (contentView.measuredWidth - descriptionTextView.measuredWidth) / 2,
            convertDpToPx(40)
        )

        codeTextView.layoutToTopLeft(
            (contentView.measuredWidth - codeTextView.measuredWidth) / 2,
            descriptionTextView.bottom + convertDpToPx(20)
        )

        boCodeExpiration.layoutToTopLeft(
            (contentView.measuredWidth - boCodeExpiration.measuredWidth) / 2,
            codeTextView.bottom + convertDpToPx(20)
        )

        timestampTextView.layoutToTopLeft(
            (contentView.measuredWidth - timestampTextView.measuredWidth) / 2,
            boCodeExpiration.bottom + convertDpToPx(20)
        )
    }
}