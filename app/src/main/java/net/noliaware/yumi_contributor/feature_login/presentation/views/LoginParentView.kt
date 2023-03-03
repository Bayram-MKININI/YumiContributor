package net.noliaware.yumi_contributor.feature_login.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ViewAnimator
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft

class LoginParentView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var backgroundView: ImageView
    private lateinit var loginViewAnimator: ViewAnimator

    lateinit var loginView: LoginView
        private set

    lateinit var passwordView: PasswordView
        private set

    private lateinit var rightViewOut: Animation
    private lateinit var rightViewIn: Animation

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        backgroundView = findViewById(R.id.background_view)
        loginViewAnimator = findViewById(R.id.login_view_animator)
        loginView = loginViewAnimator.findViewById(R.id.login_view)
        passwordView = loginViewAnimator.findViewById(R.id.password_view)

        rightViewIn = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
        rightViewOut = AnimationUtils.loadAnimation(context, R.anim.slide_out_left)
    }

    fun setLogin(login: String) {
        loginView.setLogin(login)
    }

    fun setLoginViewProgressVisible(visible: Boolean) {
        loginView.setProgressVisible(visible)
    }

    fun displayPasswordView() {
        loginViewAnimator.inAnimation = rightViewIn
        loginViewAnimator.outAnimation = rightViewOut
        loginViewAnimator.showNext()
    }

    fun fillPadViewWithData(padDigits: List<Int>) {
        passwordView.fillPadViewWithData(padDigits)
    }

    fun addSecretDigit() {
        passwordView.addSecretDigit()
    }

    fun clearSecretDigits() {
        passwordView.clearSecretDigits()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        backgroundView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )

        loginViewAnimator.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        backgroundView.layoutToTopLeft(0, 0)

        loginViewAnimator.layoutToTopLeft(
            0,
            (viewHeight - loginViewAnimator.measuredHeight) / 2
        )
    }
}