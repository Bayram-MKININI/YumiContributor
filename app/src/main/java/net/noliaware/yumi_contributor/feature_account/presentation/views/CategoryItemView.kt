package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.views.ElevatedCardView
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.drawableIdByName
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight
import net.noliaware.yumi_contributor.commun.util.measureWrapContent

class CategoryItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ElevatedCardView(context, attrs, defStyle) {

    private lateinit var countTextView: TextView
    private lateinit var iconImageView: ImageView
    private lateinit var titleTextView: TextView

    data class CategoryItemViewAdapter(
        val count: String = "",
        val iconName: String?,
        val title: String = ""
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        countTextView = findViewById(R.id.count_text_view)
        iconImageView = findViewById(R.id.icon_image_view)
        titleTextView = findViewById(R.id.title_text_view)
    }

    fun fillViewWithData(categoryItemViewAdapter: CategoryItemViewAdapter) {
        countTextView.text = categoryItemViewAdapter.count
        iconImageView.setImageResource(context.drawableIdByName(categoryItemViewAdapter.iconName))
        titleTextView.text = categoryItemViewAdapter.title
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        countTextView.measureWrapContent()

        val iconSize = viewWidth / 2
        iconImageView.measure(
            MeasureSpec.makeMeasureSpec(iconSize, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(iconSize, MeasureSpec.EXACTLY)
        )

        titleTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 9 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        countTextView.layoutToTopRight(
            viewWidth - convertDpToPx(10),
            convertDpToPx(10)
        )

        iconImageView.layoutToTopLeft(
            (viewWidth - iconImageView.measuredWidth) / 2,
            (viewHeight - iconImageView.measuredHeight) / 2
        )

        titleTextView.layoutToTopLeft(
            (viewHeight - titleTextView.measuredWidth) / 2,
            iconImageView.bottom + convertDpToPx(2)
        )
    }
}