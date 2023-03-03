package net.noliaware.yumi_contributor.feature_login.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent

class LoginViewContainer(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var iconView: ImageView
    private lateinit var loginView: LoginView

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        iconView = findViewById(R.id.icon_view)
        loginView = findViewById(R.id.login_view)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)

        iconView.measureWrapContent()

        val rectangleWidth = viewWidth * 9 / 10

        loginView.measure(
            MeasureSpec.makeMeasureSpec(rectangleWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val viewHeight = loginView.measuredHeight + iconView.measuredHeight

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        loginView.layoutToTopLeft(
            (viewWidth - loginView.measuredWidth) / 2,
            iconView.measuredHeight / 2
        )

        iconView.layoutToTopLeft(loginView.left, 0)
    }
}