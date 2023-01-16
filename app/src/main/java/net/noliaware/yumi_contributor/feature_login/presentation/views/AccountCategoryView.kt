package net.noliaware.yumi_contributor.feature_login.presentation.views

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.*

class AccountCategoryView(context: Context) : ViewGroup(context) {

    private lateinit var iconImageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var countTextView: TextView

    data class AccountCategoryViewAdapter(
        val iconName: String = "",
        val title: String = "",
        val count: Int = 0
    )

    init {
        initView()
    }

    private fun initView() {

        LayoutInflater.from(context).also {
            it.inflate(R.layout.category_layout, this, true)
        }

        iconImageView = findViewById(R.id.icon_image_view)
        titleTextView = findViewById(R.id.title_text_view)
        countTextView = findViewById(R.id.count_text_view)
    }

    fun fillViewWithData(accountCategoryViewAdapter: AccountCategoryViewAdapter) {
        iconImageView.setImageResource(context.drawableIdByName(accountCategoryViewAdapter.iconName))
        titleTextView.text = accountCategoryViewAdapter.title
        countTextView.text = accountCategoryViewAdapter.count.toString()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        iconImageView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(35), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(35), MeasureSpec.EXACTLY)
        )

        titleTextView.measureWrapContent()

        countTextView.measureWrapContent()

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(50), MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        iconImageView.layoutToTopLeft(
            0,
            (viewHeight - iconImageView.measuredHeight) / 2
        )

        titleTextView.layoutToTopLeft(
            iconImageView.right + iconImageView.measuredWidth / 2,
            (viewHeight - titleTextView.measuredHeight) / 2
        )

        countTextView.layoutToTopRight(
            viewWidth,
            (viewHeight - countTextView.measuredHeight) / 2
        )
    }
}