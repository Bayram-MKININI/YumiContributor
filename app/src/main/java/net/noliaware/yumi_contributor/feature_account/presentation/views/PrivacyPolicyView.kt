package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.webkit.WebViewClientCompat
import com.google.android.material.progressindicator.LinearProgressIndicator
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.UI.GOLDEN_RATIO
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.getStatusBarHeight
import net.noliaware.yumi_contributor.commun.util.layoutToBottomLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import kotlin.math.roundToInt

class PrivacyPolicyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var backgroundView: View
    private lateinit var headerView: View
    private lateinit var titleTextView: TextView
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var contentView: View
    private lateinit var iconImageView: ImageView
    private lateinit var offlineMessageTextView: TextView
    private lateinit var webView: WebView
    private lateinit var confirmationButton: View
    var callback: PrivacyPolicyViewCallback? = null

    fun interface PrivacyPolicyViewCallback {
        fun onConfirmationButtonClicked()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        backgroundView = findViewById(R.id.background_view)
        headerView = findViewById(R.id.header_view)
        titleTextView = findViewById(R.id.title_text_view)
        progressBar = findViewById(R.id.progress_bar)
        contentView = findViewById(R.id.content_layout)
        iconImageView = findViewById(R.id.icon_image_view)
        offlineMessageTextView = findViewById(R.id.offline_message)

        webView = findViewById(R.id.web_view)
        webView.webChromeClient = webChromeClient
        webView.webViewClient = webViewClient
        confirmationButton = findViewById(R.id.confirmation_layout)
        confirmationButton.setOnClickListener {
            callback?.onConfirmationButtonClicked()
        }
    }

    fun loadURL(url: String) {
        webView.loadUrl(url)
    }

    private val webChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(
            view: WebView,
            newProgress: Int
        ) {
            progressBar.setProgressCompat(newProgress, true)
            super.onProgressChanged(view, newProgress)
        }
    }

    private val webViewClient: WebViewClient = object : WebViewClientCompat() {
        override fun onPageStarted(
            view: WebView?,
            url: String?,
            favicon: Bitmap?
        ) {
            progressBar.isVisible = true
        }
        override fun onPageFinished(
            view: WebView,
            url: String
        ) {
            progressBar.isGone = true
        }
    }

    fun showWebView() {
        iconImageView.isGone = true
        offlineMessageTextView.isGone = true
        webView.isVisible = true
    }

    fun showOfflineMessage() {
        iconImageView.isVisible = true
        offlineMessageTextView.isVisible = true
        webView.isGone = true
    }

    fun destroyWebPage() {
        webView.destroy()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        backgroundView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight - getStatusBarHeight(), MeasureSpec.EXACTLY)
        )

        headerView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(
                getStatusBarHeight() + convertDpToPx(48), MeasureSpec.EXACTLY
            )
        )

        titleTextView.measureWrapContent()

        if (progressBar.isVisible) {
            progressBar.measure(
                MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            progressBar.trackCornerRadius = progressBar.measuredHeight / 2
        }

        val contentViewWidth = viewWidth * 95 / 100
        val sideMargin = viewWidth * 5 / 100 / 2
        val contentViewHeight = viewHeight - (headerView.measuredHeight + sideMargin * 2)
        contentView.measure(
            MeasureSpec.makeMeasureSpec(contentViewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(contentViewHeight, MeasureSpec.EXACTLY)
        )

        confirmationButton.measureWrapContent()

        val webViewWidth = contentViewWidth * 95 / 100
        val webViewHeight = contentViewHeight - (confirmationButton.measuredHeight +
                contentViewHeight * 5 / 100 / 2 + convertDpToPx(20))
        webView.measure(
            MeasureSpec.makeMeasureSpec(webViewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(webViewHeight, MeasureSpec.EXACTLY)
        )

        iconImageView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(100), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        offlineMessageTextView.measureWrapContent()

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        backgroundView.layoutToTopLeft(
            0,
            getStatusBarHeight()
        )

        headerView.layoutToTopLeft(0, 0)

        if (progressBar.isVisible) {
            progressBar.layoutToTopLeft(0, headerView.bottom)
        }

        titleTextView.layoutToTopLeft(
            (viewWidth - titleTextView.measuredWidth) / 2,
            getStatusBarHeight() +
                    (headerView.measuredHeight - (getStatusBarHeight() + titleTextView.measuredHeight)) / 2
        )

        val sideMargin = viewWidth * 5 / 100 / 2
        contentView.layoutToTopLeft(
            (viewWidth - contentView.measuredWidth) / 2,
            headerView.bottom + sideMargin
        )

        val webViewSideMargin = contentView.measuredWidth * 5 / 100 / 2
        webView.layoutToTopLeft(
            (contentView.measuredWidth - webView.measuredWidth) / 2,
            webViewSideMargin
        )

        val offlineContentHeight = iconImageView.measuredHeight +
                offlineMessageTextView.measuredHeight + convertDpToPx(10)

        iconImageView.layoutToTopLeft(
            (viewWidth - iconImageView.measuredWidth) / 2,
            (contentView.measuredHeight * (1 - 1 / GOLDEN_RATIO)).roundToInt() - offlineContentHeight / 2
        )

        offlineMessageTextView.layoutToTopLeft(
            (viewWidth - offlineMessageTextView.measuredWidth) / 2,
            iconImageView.bottom + convertDpToPx(10)
        )

        confirmationButton.layoutToBottomLeft(
            (contentView.measuredWidth - confirmationButton.measuredWidth) / 2,
            contentView.measuredHeight - convertDpToPx(20)
        )
    }
}