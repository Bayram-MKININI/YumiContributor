package net.noliaware.yumi_contributor.feature_profile.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.weak
import kotlin.math.max

class ProfileView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var myDataTextView: TextView
    private lateinit var loginTitleTextView: TextView
    private lateinit var loginValueTextView: TextView
    private lateinit var surnameTitleTextView: TextView
    private lateinit var surnameValueTextView: TextView
    private lateinit var nameTitleTextView: TextView
    private lateinit var nameValueTextView: TextView
    private lateinit var phoneTitleTextView: TextView
    private lateinit var phoneValueTextView: TextView
    private lateinit var addressTitleTextView: TextView
    private lateinit var addressValueTextView: TextView

    private lateinit var separatorView: View
    private lateinit var boAccessTextView: TextView
    private lateinit var boAccessDescriptionTextView: TextView
    private lateinit var accessButtonLayout: LinearLayoutCompat
    private lateinit var privacyPolicyLinkTextView: TextView
    var callback: ProfileViewCallback? by weak()

    data class ProfileViewAdapter(
        val login: String = "",
        val surname: String = "",
        val name: String = "",
        val phone: String = "",
        val address: String = "",
        val twoFactorAuthModeText: String = "",
        val twoFactorAuthModeActivated: Boolean = false
    )

    interface ProfileViewCallback {
        fun onGetCodeButtonClicked()
        fun onPrivacyPolicyButtonClicked()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        myDataTextView = findViewById(R.id.my_data_text_view)
        loginTitleTextView = findViewById(R.id.login_title_text_view)
        loginValueTextView = findViewById(R.id.login_value_text_view)
        surnameTitleTextView = findViewById(R.id.surname_title_text_view)
        surnameValueTextView = findViewById(R.id.surname_value_text_view)
        nameTitleTextView = findViewById(R.id.name_title_text_view)
        nameValueTextView = findViewById(R.id.name_value_text_view)
        phoneTitleTextView = findViewById(R.id.phone_title_text_view)
        phoneValueTextView = findViewById(R.id.phone_value_text_view)
        addressTitleTextView = findViewById(R.id.address_title_text_view)
        addressValueTextView = findViewById(R.id.address_value_text_view)

        separatorView = findViewById(R.id.separator_view)
        boAccessTextView = findViewById(R.id.bo_access_text_view)
        boAccessDescriptionTextView = findViewById(R.id.bo_access_description_text_view)
        accessButtonLayout = findViewById(R.id.access_button_layout)
        accessButtonLayout.setOnClickListener {
            callback?.onGetCodeButtonClicked()
        }
        privacyPolicyLinkTextView = findViewById(R.id.privacy_policy_link_text_view)
        privacyPolicyLinkTextView.setOnClickListener {
            callback?.onPrivacyPolicyButtonClicked()
        }
    }

    fun fillViewWithData(profileViewAdapter: ProfileViewAdapter) {
        loginValueTextView.text = profileViewAdapter.login
        surnameValueTextView.text = profileViewAdapter.surname
        nameValueTextView.text = profileViewAdapter.name
        phoneValueTextView.text = profileViewAdapter.phone
        addressValueTextView.text = profileViewAdapter.address

        boAccessDescriptionTextView.text = profileViewAdapter.twoFactorAuthModeText
        if (profileViewAdapter.twoFactorAuthModeActivated) {
            boAccessDescriptionTextView.gravity = Gravity.CENTER
        }
        accessButtonLayout.isVisible = profileViewAdapter.twoFactorAuthModeActivated
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        myDataTextView.measureWrapContent()

        loginTitleTextView.measureWrapContent()
        loginValueTextView.measureWrapContent()

        surnameTitleTextView.measureWrapContent()
        surnameValueTextView.measureWrapContent()

        nameTitleTextView.measureWrapContent()
        nameValueTextView.measureWrapContent()

        phoneTitleTextView.measureWrapContent()
        phoneValueTextView.measureWrapContent()

        addressTitleTextView.measureWrapContent()
        addressValueTextView.measureWrapContent()

        separatorView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 4 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(3), MeasureSpec.EXACTLY)
        )

        boAccessTextView.measureWrapContent()
        boAccessDescriptionTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth - convertDpToPx(40), MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        if (accessButtonLayout.isVisible) {
            accessButtonLayout.measureWrapContent()
        }

        privacyPolicyLinkTextView.measureWrapContent()

        viewHeight = myDataTextView.measuredHeight +
                max(loginTitleTextView.measuredHeight, loginValueTextView.measuredHeight) +
                max(surnameTitleTextView.measuredHeight, surnameValueTextView.measuredHeight) +
                max(nameTitleTextView.measuredHeight, nameValueTextView.measuredHeight) +
                max(phoneTitleTextView.measuredHeight, phoneValueTextView.measuredHeight) +
                max(addressTitleTextView.measuredHeight, addressValueTextView.measuredHeight) +
                separatorView.measuredHeight + boAccessTextView.measuredHeight +
                boAccessDescriptionTextView.measuredHeight +
                if (accessButtonLayout.isVisible) {
                    accessButtonLayout.measuredHeight + convertDpToPx(15)
                } else {
                    0
                } +
                privacyPolicyLinkTextView.measuredHeight + convertDpToPx(125)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        myDataTextView.layoutToTopLeft(
            convertDpToPx(20),
            0
        )

        val edge = viewWidth * 1 / 3

        loginTitleTextView.layoutToTopRight(
            edge,
            myDataTextView.bottom + convertDpToPx(15)
        )

        loginValueTextView.layoutToTopLeft(
            loginTitleTextView.right + convertDpToPx(15),
            loginTitleTextView.top
        )

        surnameTitleTextView.layoutToTopRight(
            edge,
            loginTitleTextView.bottom + convertDpToPx(10)
        )

        surnameValueTextView.layoutToTopLeft(
            surnameTitleTextView.right + convertDpToPx(15),
            surnameTitleTextView.top
        )

        nameTitleTextView.layoutToTopRight(
            edge,
            surnameTitleTextView.bottom + convertDpToPx(10)
        )

        nameValueTextView.layoutToTopLeft(
            nameTitleTextView.right + convertDpToPx(15),
            nameTitleTextView.top
        )

        phoneTitleTextView.layoutToTopRight(
            edge,
            nameTitleTextView.bottom + convertDpToPx(10)
        )

        phoneValueTextView.layoutToTopLeft(
            phoneTitleTextView.right + convertDpToPx(15),
            phoneTitleTextView.top
        )

        addressTitleTextView.layoutToTopRight(
            edge,
            phoneValueTextView.bottom + convertDpToPx(10)
        )

        addressValueTextView.layoutToTopLeft(
            addressTitleTextView.right + convertDpToPx(15),
            addressTitleTextView.top
        )

        separatorView.layoutToTopLeft(
            (viewWidth - separatorView.measuredWidth) / 2,
            max(addressTitleTextView.bottom, addressValueTextView.bottom) + convertDpToPx(15)
        )

        boAccessTextView.layoutToTopLeft(
            myDataTextView.left,
            separatorView.bottom + convertDpToPx(15)
        )

        boAccessDescriptionTextView.layoutToTopLeft(
            myDataTextView.left,
            boAccessTextView.bottom + convertDpToPx(10)
        )
        val boAccessBottom = if (accessButtonLayout.isVisible) {
            accessButtonLayout.layoutToTopLeft(
                (viewWidth - accessButtonLayout.measuredWidth) / 2,
                boAccessDescriptionTextView.bottom + convertDpToPx(15)
            )
            accessButtonLayout.bottom
        } else {
            boAccessDescriptionTextView.bottom
        }

        privacyPolicyLinkTextView.layoutToTopLeft(
            (viewWidth - privacyPolicyLinkTextView.measuredWidth) / 2,
            boAccessBottom + convertDpToPx(30)
        )
    }
}