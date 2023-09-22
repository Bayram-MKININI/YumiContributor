package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.facebook.shimmer.ShimmerFrameLayout
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.activateShimmer
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.weak

class SelectedAccountParentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var backLayout: View
    private lateinit var shimmerView: ShimmerFrameLayout
    private lateinit var selectedAccountView: SelectedAccountView

    val getViewPager get() = selectedAccountView.getViewPager

    var callback: SelectedAccountViewCallback? by weak()

    fun interface SelectedAccountViewCallback {
        fun onBackButtonClicked()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        backLayout = findViewById(R.id.back_layout)
        backLayout.setOnClickListener { callback?.onBackButtonClicked() }
        shimmerView = findViewById(R.id.shimmer_view)
        selectedAccountView = shimmerView.findViewById(R.id.selected_account_view)
    }

    fun activateLoading(visible: Boolean) {
        shimmerView.activateShimmer(visible)
        if (visible) {
            selectedAccountView.setLoadingView()
        }
    }

    fun setTitle(title: String) {
        selectedAccountView.setTitle(title)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        backLayout.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(44), MeasureSpec.EXACTLY)
        )

        val shimmerViewHeight = viewHeight - (backLayout.measuredHeight + convertDpToPx(10))

        shimmerView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(shimmerViewHeight, MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        backLayout.layoutToTopLeft(0, 0)

        shimmerView.layoutToTopLeft(
            0,
            backLayout.bottom + convertDpToPx(10)
        )
    }
}