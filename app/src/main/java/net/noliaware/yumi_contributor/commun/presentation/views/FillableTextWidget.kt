package net.noliaware.yumi_contributor.commun.presentation.views

import android.content.Context
import android.text.SpannableString
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent

class FillableTextWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var placeholderView: View
    private var isFixedWidth: Boolean = false
    lateinit var textView: TextView
        private set

    init {
        initView()
    }

    private fun initView() {
        inflate(R.layout.fillable_text_layout, true)
        placeholderView = findViewById(R.id.placeholder_view)
        textView = findViewById(R.id.text_view)
    }

    fun setText(title: String) {
        textView.text = title
        placeholderView.isGone = true
        textView.isVisible = true
    }

    fun setText(title: SpannableString?) {
        textView.text = title
        placeholderView.isGone = true
        textView.isVisible = true
    }

    fun resetText() {
        textView.text = ""
        textView.isGone = true
        placeholderView.isVisible = true
    }

    fun setFixedWidth(fixed: Boolean) {
        isFixedWidth = fixed
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        if (placeholderView.isVisible) {
            placeholderView.measure(
                MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
            )
        }
        if (textView.isVisible) {
            if (isFixedWidth) {
                textView.measure(
                    MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                )
            } else {
                textView.measureWrapContent()
            }
            viewWidth = textView.measuredWidth
            viewHeight = textView.measuredHeight
        }

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top
        if (placeholderView.isVisible) {
            placeholderView.layoutToTopLeft(0, 0)
        }
        if (textView.isVisible) {
            textView.layoutToTopLeft(0, 0)
        }
    }
}