package net.noliaware.yumi_contributor.commun.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.getColorCompat
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import java.lang.Integer.max

class ClipartTabView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var titleTextView: TextView

    init {
        initView()
    }

    private fun initView() {
        inflate(
            layoutRes = R.layout.clipart_tab_layout,
            attachToRoot = true
        )
        setBackgroundResource(R.drawable.clipart_white)
        titleTextView = findViewById(R.id.title_text_view)
    }

    fun setTitle(title: String) {
        titleTextView.text = title
    }

    fun setTabSelected(selected: Boolean) {
        elevation = if (selected) {
            titleTextView.setTextColor(context.getColorCompat(R.color.colorPrimary))
            convertDpToPx(16) * 1f
        } else {
            titleTextView.setTextColor(context.getColorCompat(R.color.grey_2))
            0f
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        titleTextView.measureWrapContent()

        val resolvedViewWidth = titleTextView.measuredWidth + convertDpToPx(20)
        val resolvedViewHeight = titleTextView.measuredHeight + convertDpToPx(40)

        viewWidth = max(viewWidth, resolvedViewWidth)
        viewHeight = max(viewHeight, resolvedViewHeight)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        titleTextView.layoutToTopLeft(
            (viewWidth - titleTextView.measuredWidth) / 2,
            convertDpToPx(10)
        )
    }
}