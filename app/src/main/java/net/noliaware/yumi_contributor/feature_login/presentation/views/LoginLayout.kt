package net.noliaware.yumi_contributor.feature_login.presentation.views

import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.MotionLayout.TransitionListener
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.getLocationRectOnScreen
import net.noliaware.yumi_contributor.commun.util.translateYByValue

class LoginLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : MotionLayout(context, attrs, defStyle), TransitionListener {

    private lateinit var backgroundView: ImageView
    private lateinit var intermediateView: View
    private lateinit var iconView: ImageView
    private lateinit var loginView: LoginView
    private lateinit var passwordView: PasswordView
    private val visibleBounds = Rect()

    val getLoginView get() = loginView
    val getPasswordView get() = passwordView

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        backgroundView = findViewById(R.id.background_view)
        intermediateView = findViewById(R.id.intermediate_view)
        iconView = findViewById(R.id.icon_view)
        loginView = findViewById(R.id.login_view)
        passwordView = findViewById(R.id.password_view)
        setTransitionListener(this)

        ViewCompat.setOnApplyWindowInsetsListener(loginView) { _, insets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                loginView.requestLayout()
            }
            insets
        }
    }

    fun computeLoginLayout() {
        postDelayed(
            {
                requestLayout()
            }, 150
        )
    }

    fun setLogin(login: String) {
        loginView.setLogin(login)
    }

    fun setLoginViewProgressVisible(visible: Boolean) {
        loginView.setProgressVisible(visible)
    }

    fun setPasswordViewProgressVisible(visible: Boolean) {
        passwordView.setProgressVisible(visible)
    }

    fun displayPasswordView() {
        transitionToEnd()
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

    override fun onTransitionStarted(
        motionLayout: MotionLayout?,
        startId: Int,
        endId: Int
    ) {
        intermediateView.isVisible = true
        passwordView.isVisible = true
        loginView.setStartAnimationState()
        passwordView.setStartAnimationState()
    }

    override fun onTransitionChange(
        motionLayout: MotionLayout?,
        startId: Int,
        endId: Int,
        progress: Float
    ) = Unit

    override fun onTransitionCompleted(
        motionLayout: MotionLayout?,
        currentId: Int
    ) {
        intermediateView.isGone = true
        loginView.setEndAnimationState()
        passwordView.setEndAnimationState()
        loginView.isGone = true
    }

    override fun onTransitionTrigger(
        motionLayout: MotionLayout?,
        triggerId: Int,
        positive: Boolean,
        progress: Float
    ) = Unit

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val viewWidth = right - left
        val viewHeight = bottom - top

        post {
            getWindowVisibleDisplayFrame(visibleBounds)
            val heightDiff = height - visibleBounds.height()
            val marginOfError = convertDpToPx(50)
            val isKeyboardOpen = heightDiff > marginOfError

            if (isKeyboardOpen) {
                val loginViewRect = loginView.getLocationRectOnScreen()
                val isLoginViewHidden = loginViewRect.bottom > visibleBounds.bottom
                if (isLoginViewHidden) {
                    animateLoginTranslationYWithValue(
                        (visibleBounds.bottom - loginViewRect.bottom).toFloat()
                    )
                }
            } else {
                animateLoginTranslationYWithValue(0f)
            }
        }
    }

    private fun animateLoginTranslationYWithValue(translationY: Float) {
        val iconAnimator = iconView.translateYByValue(translationY)
        val loginViewAnimator = loginView.translateYByValue(translationY)
        AnimatorSet().apply {
            play(loginViewAnimator).with(iconAnimator)
            duration = 100
            start()
        }
    }
}