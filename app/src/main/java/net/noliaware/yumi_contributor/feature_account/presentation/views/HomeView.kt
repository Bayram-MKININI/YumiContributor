package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.fragment.app.FragmentContainerView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent

class HomeView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var mainFragmentContainer: FragmentContainerView
    lateinit var homeMenuView: HomeMenuView
        private set

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        mainFragmentContainer = findViewById(R.id.main_fragment_container)
        homeMenuView = findViewById(R.id.menu_card_view)
    }

    fun selectHomeButton() {
        homeMenuView.selectHomeButton()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        homeMenuView.measureWrapContent()

        mainFragmentContainer.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(
                viewHeight - (homeMenuView.measuredHeight + convertDpToPx(30)),
                MeasureSpec.EXACTLY
            )
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        val viewWidth = right - left
        val viewHeight = bottom - top

        mainFragmentContainer.layoutToTopLeft(0, 0)
        homeMenuView.layoutToTopLeft(
            (viewWidth - homeMenuView.measuredWidth) / 2,
            mainFragmentContainer.bottom
        )
    }
}