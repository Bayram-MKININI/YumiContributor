package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.GOLDEN_RATIO
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import kotlin.math.roundToInt

class LocationView(context: Context) : ViewGroup(context) {

    private lateinit var titleTextView: TextView
    private lateinit var iconImageView: ImageView
    var locationClickedAction: (() -> Unit)? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    init {
        initView()
    }

    private fun initView() {

        LayoutInflater.from(context).also {
            it.inflate(R.layout.location_layout, this, true)
        }

        titleTextView = findViewById(R.id.title_text_view)
        iconImageView = findViewById(R.id.icon_image_view)
        iconImageView.setOnClickListener { locationClickedAction?.invoke() }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)

        titleTextView.measureWrapContent()

        iconImageView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(40), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val viewHeight =
            titleTextView.measuredHeight.coerceAtLeast(iconImageView.measuredHeight) + convertDpToPx(
                20
            )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        val iconImageViewLeft = (viewWidth * (1 - 1 / GOLDEN_RATIO)).roundToInt()
        iconImageView.layoutToTopLeft(
            iconImageViewLeft,
            titleTextView.top
        )

        titleTextView.layoutToTopLeft(
            0,
            (iconImageView.measuredHeight - titleTextView.measuredHeight) / 2
        )

    }
}