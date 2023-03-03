package net.noliaware.yumi_contributor.feature_profile.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight
import net.noliaware.yumi_contributor.commun.util.measureWrapContent

class ProfileView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var myDataTextView: TextView
    private lateinit var loginTitleTextView: TextView
    private lateinit var loginValueTextView: TextView
    private lateinit var surnameTitleTextView: TextView
    private lateinit var surnameValueTextView: TextView
    private lateinit var nameTitleTextView: TextView
    private lateinit var nameValueTextView: TextView
    private lateinit var separatorView: View
    private lateinit var complementaryDataTextView: TextView
    private lateinit var phoneTitleTextView: TextView
    private lateinit var phoneValueTextView: TextView
    private lateinit var addressTitleTextView: TextView
    private lateinit var addressValueTextView: TextView

    data class ProfileViewAdapter(
        val login: String = "",
        val surname: String = "",
        val name: String = "",
        val phone: String = "",
        val address: String = ""
    )

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
        separatorView = findViewById(R.id.separator_view)
        complementaryDataTextView = findViewById(R.id.complementary_data_text_view)
        phoneTitleTextView = findViewById(R.id.phone_title_text_view)
        phoneValueTextView = findViewById(R.id.phone_value_text_view)
        addressTitleTextView = findViewById(R.id.address_title_text_view)
        addressValueTextView = findViewById(R.id.address_value_text_view)
    }

    fun fillViewWithData(profileViewAdapter: ProfileViewAdapter) {
        loginValueTextView.text = profileViewAdapter.login
        surnameValueTextView.text = profileViewAdapter.surname
        nameValueTextView.text = profileViewAdapter.name
        phoneValueTextView.text = profileViewAdapter.phone
        addressValueTextView.text = profileViewAdapter.address
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

        separatorView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 4 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(3), MeasureSpec.EXACTLY)
        )

        complementaryDataTextView.measureWrapContent()

        phoneTitleTextView.measureWrapContent()
        phoneValueTextView.measureWrapContent()

        addressTitleTextView.measureWrapContent()
        addressValueTextView.measureWrapContent()

        viewHeight = myDataTextView.measuredHeight + loginValueTextView.measuredHeight + surnameValueTextView.measuredHeight +
                    nameValueTextView.measuredHeight + separatorView.measuredHeight + complementaryDataTextView.measuredHeight +
                    phoneValueTextView.measuredHeight + addressValueTextView.measuredHeight + convertDpToPx(95)

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

        separatorView.layoutToTopLeft(
            (viewWidth - separatorView.measuredWidth) / 2,
            nameValueTextView.bottom + convertDpToPx(20)
        )

        complementaryDataTextView.layoutToTopLeft(
            myDataTextView.left,
            separatorView.bottom + convertDpToPx(20)
        )

        phoneTitleTextView.layoutToTopRight(
            edge,
            complementaryDataTextView.bottom + convertDpToPx(10)
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
    }
}