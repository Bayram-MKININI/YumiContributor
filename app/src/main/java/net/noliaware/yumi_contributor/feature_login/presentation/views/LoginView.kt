package net.noliaware.yumi_contributor.feature_login.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputLayout
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.views.ElevatedCardView
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.hideKeyboard
import net.noliaware.yumi_contributor.commun.util.layoutToBottomLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.weak

class LoginView(context: Context, attrs: AttributeSet?) : ElevatedCardView(context, attrs) {

    private lateinit var inputMessageTextView: TextView
    private lateinit var inputLayoutLogin: TextInputLayout
    private lateinit var inputLogin: EditText
    private lateinit var confirmImageView: ImageView
    private lateinit var confirmTextView: TextView
    private lateinit var progressBar: ProgressBar

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
        inputLayoutLogin = findViewById(R.id.input_layout_login)

        inputLogin = inputLayoutLogin.findViewById(R.id.input_login)
        inputLogin.setOnEditorActionListener(onEditorActionListener)
        inputLogin.doAfterTextChanged {
            if (inputLogin.text.isNullOrEmpty()) {
                inputLogin.error = null
                inputLayoutLogin.isErrorEnabled = false
            }
        }

        confirmImageView = findViewById(R.id.confirm_image_view)
        confirmImageView.setOnClickListener {
            confirmInputText()
        }

        confirmTextView = findViewById(R.id.confirm_text_view)
        progressBar = findViewById(R.id.progress_bar)
    }

    fun setLogin(login: String) {
        inputLogin.setText(login)
    }

    fun setProgressVisible(visible: Boolean) {
        if (visible) {
            progressBar.visibility = VISIBLE
        } else {
            progressBar.visibility = GONE
        }
    }

    private val onEditorActionListener = OnEditorActionListener { _, actionId, _ ->

        if (actionId == EditorInfo.IME_ACTION_DONE) {

            if (validateLogin()) {
                //confirmTextView.requestFocus()
                context.hideKeyboard()
                confirmInputText()
            }
        }

        false
    }

    private fun confirmInputText() {
        callback?.onLoginEntered(
            inputLogin.text.toString().trim()
        )
    }

    private fun validateLogin(): Boolean {

        val login = inputLogin.text.toString().trim()

        if (login.isEmpty()) {

            inputLayoutLogin.error = context.getString(R.string.login_empty_error)
            return false

        } else {

            inputLayoutLogin.isErrorEnabled = false
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = View.MeasureSpec.getSize(heightMeasureSpec)

        inputMessageTextView.measureWrapContent()

        inputLayoutLogin.measure(
            View.MeasureSpec.makeMeasureSpec(viewWidth * 8 / 10, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )

        confirmImageView.measureWrapContent()
        confirmTextView.measureWrapContent()

        progressBar.measureWrapContent()

        viewHeight = inputMessageTextView.measuredHeight + inputLayoutLogin.measuredHeight +
                confirmImageView.measuredHeight + convertDpToPx(108)

        setMeasuredDimension(
            View.MeasureSpec.makeMeasureSpec(viewWidth, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(viewHeight, View.MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        inputMessageTextView.layoutToTopLeft(
            (viewWidth - inputMessageTextView.measuredWidth) / 2,
            convertDpToPx(40)
        )

        inputLayoutLogin.layoutToTopLeft(
            (viewWidth - inputLayoutLogin.measuredWidth) / 2,
            inputMessageTextView.bottom + convertDpToPx(12)
        )

        confirmImageView.layoutToTopLeft(
            (viewWidth - confirmImageView.measuredWidth) / 2,
            inputLayoutLogin.bottom + convertDpToPx(16)
        )

        confirmTextView.layoutToTopLeft(
            (viewWidth - confirmTextView.measuredWidth) / 2,
            confirmImageView.top + convertDpToPx(7)
        )

        progressBar.layoutToBottomLeft(
            (viewWidth - progressBar.measuredWidth) / 2,
            viewHeight - convertDpToPx(7)
        )
    }
}