package net.noliaware.yumi_contributor.feature_profile.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.getStatusBarHeight
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent

class ProfileParentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var headerView: View
    private lateinit var titleTextView: TextView
    private lateinit var profileIconView: View
    private lateinit var shimmerView: ShimmerFrameLayout
    private lateinit var contentView: View
    private lateinit var profileView: ProfileView

    val getProfileView get() = profileView

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        headerView = findViewById(R.id.header_view)
        titleTextView = findViewById(R.id.title_text_view)
        profileIconView = findViewById(R.id.profile_icon_view)
        contentView = findViewById(R.id.content_layout)
        shimmerView = contentView.findViewById(R.id.shimmer_view)
        profileView = shimmerView.findViewById(R.id.profile_view)
    }

    fun setLoadingVisible(visible: Boolean) {
        shimmerView.setShimmer(
            Shimmer.AlphaHighlightBuilder()
                .setBaseAlpha(if (visible) 0.4f else 1f)
                .setDuration(resources.getInteger(R.integer.shimmer_animation_duration_ms).toLong())
                .build()
        )
        if (visible) {
            shimmerView.startShimmer()
        } else {
            shimmerView.stopShimmer()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        headerView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(
                getStatusBarHeight() + convertDpToPx(75),
                MeasureSpec.EXACTLY
            )
        )

        titleTextView.measureWrapContent()
        profileIconView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(50), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(50), MeasureSpec.EXACTLY)
        )

        val contentViewWidth = viewWidth * 95 / 100
        val sideMargin = viewWidth * 5 / 100 / 2
        val contentViewHeight = viewHeight - (headerView.measuredHeight + profileIconView.measuredHeight / 2 +
                sideMargin + convertDpToPx(35))
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

        headerView.layoutToTopLeft(0, 0)

        titleTextView.layoutToTopLeft(
            (viewWidth - titleTextView.measuredWidth) / 2,
            getStatusBarHeight() + convertDpToPx(15)
        )

        profileIconView.layoutToTopLeft(
            (viewWidth - profileIconView.measuredWidth) / 2,
            headerView.bottom - profileIconView.measuredHeight / 2
        )

        contentView.layoutToTopLeft(
            (viewWidth - contentView.measuredWidth) / 2,
            profileIconView.bottom + convertDpToPx(15)
        )
    }
}