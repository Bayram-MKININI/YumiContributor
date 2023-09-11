package net.noliaware.yumi_contributor.feature_message.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent

class MessagePriorityDropdownItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var iconImageView: ImageView
    private lateinit var titleTextView: TextView

    data class MessagePriorityDropdownItemViewAdapter(
        val iconDrawable: Int,
        val title: String
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        iconImageView = findViewById(R.id.priority_icon_image_view)
        titleTextView = findViewById(R.id.priority_text_view)
    }

    fun fillViewWithData(adapter: MessagePriorityDropdownItemViewAdapter) {
        iconImageView.setImageResource(adapter.iconDrawable)
        titleTextView.text = adapter.title
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        iconImageView.measureWrapContent()
        titleTextView.measureWrapContent()

        val viewHeight = convertDpToPx(37)
        val viewWidth = convertDpToPx(150)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        iconImageView.layoutToTopLeft(
            convertDpToPx(10),
            (viewHeight - iconImageView.measuredHeight) / 2
        )

        titleTextView.layoutToTopLeft(
            iconImageView.right + convertDpToPx(10),
            (viewHeight - titleTextView.measuredHeight) / 2
        )
    }
}