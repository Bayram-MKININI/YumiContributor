package net.noliaware.yumi_contributor.feature_profile.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.*

class ProfileParentView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var headerView: View
    private lateinit var titleTextView: TextView
    private lateinit var profileIconView: View
    private lateinit var contentView: View
    private lateinit var profileDataView: View
    private lateinit var profileView: ProfileView
    private lateinit var getCodeLayout: LinearLayoutCompat

    val getProfileView get() = profileView
    var callback: ProfileParentViewCallback? by weak()

    fun interface ProfileParentViewCallback {
        fun onGetCodeButtonClicked()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        headerView = findViewById(R.id.header_view)
        titleTextView = findViewById(R.id.title_text_view)
        profileIconView = findViewById(R.id.profile_icon_view)
        contentView = findViewById(R.id.content_layout)
        profileDataView = contentView.findViewById(R.id.profile_data_view)
        profileView = profileDataView.findViewById(R.id.profile_view)
        getCodeLayout = findViewById(R.id.get_code_layout)
        getCodeLayout.setOnClickListener { callback?.onGetCodeButtonClicked() }
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

        getCodeLayout.measureWrapContent()

        val contentViewWidth = viewWidth * 9 / 10
        val contentViewHeight = viewHeight - (headerView.measuredHeight + profileIconView.measuredHeight / 2 + convertDpToPx(55))
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

        getCodeLayout.layoutToBottomLeft(
            (contentView.measuredWidth - getCodeLayout.measuredWidth) / 2,
            contentView.measuredHeight - convertDpToPx(40)
        )
    }
}