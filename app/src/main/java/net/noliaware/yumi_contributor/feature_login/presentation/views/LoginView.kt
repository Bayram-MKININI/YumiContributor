package net.noliaware.yumi_contributor.feature_login.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.textfield.TextInputLayout
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.views.ElevatedCardView
import net.noliaware.yumi_contributor.commun.util.activateShimmer
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.hideKeyboard
import net.noliaware.yumi_contributor.commun.util.isLoginNotValid
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.weak

class LoginView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ElevatedCardView(context, attrs, defStyle) {

    private lateinit var inputMessageTextView: TextView
    private lateinit var inputLayoutLoginShimmerView: ShimmerFrameLayout
    private lateinit var inputLayoutLogin: TextInputLayout
    private lateinit var inputLogin: EditText
    private lateinit var confirmImageView: ImageView
    private lateinit var confirmTextView: TextView

    var callback: LoginViewCallback? by weak()

    fun interface LoginViewCallback {
        fun onLoginEntered(login: String)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {

        inputMessageTextView = findViewById(R.id.input_message_text_view)
        inputLayoutLoginShimmerView = findViewById(R.id.input_layout_login_shimmer_view)
        inputLayoutLogin = inputLayoutLoginShimmerView.findViewById(R.id.input_layout_login)

        inputLogin = inputLayoutLogin.findViewById(R.id.input_login)
        inputLogin.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (validateLogin()) {
                    confirmInputText()
                }
            }
            false
        }

        inputLogin.doAfterTextChanged {
            if (inputLogin.text.isNullOrEmpty()) {
                inputLogin.error = null
                inputLayoutLogin.isErrorEnabled = false
            }
        }

        confirmImageView = findViewById(R.id.confirm_image_view)
        confirmImageView.setOnClickListener {
            if (validateLogin()) {
                confirmInputText()
            }
        }

        confirmTextView = findViewById(R.id.confirm_text_view)
    }

    fun setLogin(login: String) {
        inputLogin.setText(login)
    }

    fun setProgressVisible(visible: Boolean) {
        inputLayoutLoginShimmerView.activateShimmer(visible)
        if (visible) {
            inputLayoutLogin.isEnabled = false
            confirmImageView.isEnabled = false
        } else {
            inputLayoutLogin.isEnabled = true
            confirmImageView.isEnabled = true
        }
    }

    private fun confirmInputText() {
        context.hideKeyboard()
        postDelayed(
            {
                callback?.onLoginEntered(
                    inputLogin.text.toString().trim()
                )
            },
            150
        )
    }

    private fun validateLogin(): Boolean {
        val login = inputLogin.text.toString().trim()
        when {
            login.isEmpty() -> context.getString(R.string.login_empty_error)
            login.isLoginNotValid() -> context.getString(R.string.login_unsafe_error)
            else -> null
        }?.let {
            inputLayoutLogin.error = it
            return false
        } ?: run {
            inputLayoutLogin.isErrorEnabled = false
            return true
        }
    }

    fun setStartAnimationState() {
        setBackgroundResource(android.R.color.transparent)
    }

    fun setEndAnimationState() {
        setBackgroundResource(R.drawable.rectangle_rounded_white)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        inputMessageTextView.measureWrapContent()

        inputLayoutLoginShimmerView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 8 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        confirmImageView.measureWrapContent()
        confirmTextView.measureWrapContent()

        viewHeight = inputMessageTextView.measuredHeight + inputLayoutLogin.measuredHeight +
                confirmImageView.measuredHeight + convertDpToPx(108)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        inputMessageTextView.layoutToTopLeft(
            (viewWidth - inputMessageTextView.measuredWidth) / 2,
            convertDpToPx(40)
        )

        inputLayoutLoginShimmerView.layoutToTopLeft(
            (viewWidth - inputLayoutLogin.measuredWidth) / 2,
            inputMessageTextView.bottom + convertDpToPx(12)
        )

        confirmImageView.layoutToTopLeft(
            (viewWidth - confirmImageView.measuredWidth) / 2,
            inputLayoutLoginShimmerView.bottom + convertDpToPx(16)
        )

        confirmTextView.layoutToTopLeft(
            (viewWidth - confirmTextView.measuredWidth) / 2,
            confirmImageView.top + convertDpToPx(7)
        )
    }
}