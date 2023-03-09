package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.*

class VouchersDetailsView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var titleTextView: TextView
    private lateinit var createdTextView: TextView
    private lateinit var expiryTextView: TextView
    private lateinit var separatorView: View
    private lateinit var sponsorBackgroundView: View
    private lateinit var sponsoredByTextView: TextView
    private lateinit var sponsorTextView: TextView
    lateinit var informationTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var goToTextView: TextView
    private lateinit var locationBackgroundView: View
    private lateinit var retailerTextView: TextView
    private lateinit var addressTextView: TextView
    lateinit var phoneImageView: ImageView
    private lateinit var phoneTextView: TextView
    private lateinit var mailImageView: ImageView
    private lateinit var mailTextView: TextView
    lateinit var openLocationLayout: LinearLayoutCompat

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        titleTextView = findViewById(R.id.title_text_view)
        createdTextView = findViewById(R.id.created_text_view)
        expiryTextView = findViewById(R.id.expiry_text_view)
        separatorView = findViewById(R.id.separator_view)
        sponsorBackgroundView = findViewById(R.id.sponsor_background)
        sponsoredByTextView = findViewById(R.id.sponsored_by_text_view)
        sponsorTextView = findViewById(R.id.sponsor_text_view)
        informationTextView = findViewById(R.id.information_text_view)
        descriptionTextView = findViewById(R.id.description_text_view)
        goToTextView = findViewById(R.id.go_to_text_view)
        locationBackgroundView = findViewById(R.id.location_background)
        retailerTextView = findViewById(R.id.retailer_text_view)
        addressTextView = findViewById(R.id.address_text_view)
        phoneImageView = findViewById(R.id.phone_image_view)
        phoneTextView = findViewById(R.id.phone_text_view)
        mailImageView = findViewById(R.id.mail_image_view)
        mailTextView = findViewById(R.id.mail_text_view)
        openLocationLayout = findViewById(R.id.open_location_layout)
    }

    fun fillViewWithData(vouchersDetailsViewAdapter: VouchersDetailsContainerView.VouchersDetailsViewAdapter) {

        titleTextView.text = vouchersDetailsViewAdapter.title
        createdTextView.text = vouchersDetailsViewAdapter.startDate
        expiryTextView.text = vouchersDetailsViewAdapter.endDate

        vouchersDetailsViewAdapter.partnerLabel?.let { label ->
            sponsorBackgroundView.isVisible = true
            sponsoredByTextView.isVisible = true
            sponsorTextView.isVisible = true
            informationTextView.isVisible = true
            sponsorTextView.text = label
        }
        vouchersDetailsViewAdapter.voucherDescription?.let {
            descriptionTextView.isVisible = true
            descriptionTextView.text = vouchersDetailsViewAdapter.voucherDescription
        }

        retailerTextView.text = vouchersDetailsViewAdapter.retailerLabel
        addressTextView.text = vouchersDetailsViewAdapter.retailerAddress
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        titleTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 9 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        createdTextView.measureWrapContent()
        expiryTextView.measureWrapContent()

        separatorView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 4 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(3), MeasureSpec.EXACTLY)
        )

        if (sponsorTextView.isVisible) {

            val sponsorBackgroundViewWidth = viewWidth * 9 / 10

            sponsoredByTextView.measureWrapContent()

            informationTextView.measure(
                MeasureSpec.makeMeasureSpec(convertDpToPx(20), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(convertDpToPx(20), MeasureSpec.EXACTLY)
            )

            val sponsorTextViewWidth = sponsorBackgroundViewWidth - (sponsoredByTextView.measuredWidth + informationTextView.measuredWidth +
                        convertDpToPx(35))

            sponsorTextView.measure(
                MeasureSpec.makeMeasureSpec(sponsorTextViewWidth, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )

            val sponsorBackgroundViewHeight = Integer.max(
                sponsorTextView.measuredHeight + convertDpToPx(20),
                convertDpToPx(40)
            )
            sponsorBackgroundView.measure(
                MeasureSpec.makeMeasureSpec(sponsorBackgroundViewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(sponsorBackgroundViewHeight, MeasureSpec.EXACTLY)
            )
        }

        descriptionTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 9 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        goToTextView.measureWrapContent()

        retailerTextView.measureWrapContent()

        addressTextView.measureWrapContent()

        phoneImageView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(30), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(30), MeasureSpec.EXACTLY)
        )

        phoneTextView.measureWrapContent()

        if (mailImageView.isVisible) {
            mailImageView.measure(
                MeasureSpec.makeMeasureSpec(convertDpToPx(30), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(convertDpToPx(30), MeasureSpec.EXACTLY)
            )
            mailTextView.measureWrapContent()
        }

        openLocationLayout.measureWrapContent()

        val locationBackgroundViewHeight = retailerTextView.measuredHeight + addressTextView.measuredHeight +
                    phoneImageView.measuredHeight + openLocationLayout.measuredHeight / 2 +
                    convertDpToPx(50)

        locationBackgroundView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 9 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(locationBackgroundViewHeight, MeasureSpec.EXACTLY)
        )

        val contentHeight = titleTextView.measuredHeight + createdTextView.measuredHeight + separatorView.measuredHeight +
                    if (sponsorTextView.isVisible) {
                        sponsorBackgroundView.measuredHeight + convertDpToPx(15)
                    } else {
                        0
                    } +
                    if (descriptionTextView.isVisible) {
                        descriptionTextView.measuredHeight + convertDpToPx(15)
                    } else {
                        0
                    } +
                    goToTextView.measuredHeight + locationBackgroundView.measuredHeight + openLocationLayout.measuredHeight / 2 +
                    convertDpToPx(130)


        viewHeight = Integer.max(contentHeight, viewHeight)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        val viewWidth = right - left
        val viewHeight = bottom - top

        titleTextView.layoutToTopLeft(
            convertDpToPx(20),
            0
        )

        createdTextView.layoutToTopLeft(
            titleTextView.left,
            titleTextView.bottom + convertDpToPx(10)
        )

        expiryTextView.layoutToTopLeft(
            createdTextView.right + convertDpToPx(2),
            createdTextView.top
        )

        separatorView.layoutToTopLeft(
            (viewWidth - separatorView.measuredWidth) / 2,
            expiryTextView.bottom + convertDpToPx(15)
        )

        val sponsorViewBottom = if (sponsorTextView.isVisible) {

            sponsorBackgroundView.layoutToTopLeft(
                (viewWidth - sponsorBackgroundView.measuredWidth) / 2,
                separatorView.bottom + convertDpToPx(15)
            )

            sponsoredByTextView.layoutToTopLeft(
                sponsorBackgroundView.left + convertDpToPx(15),
                sponsorBackgroundView.top + convertDpToPx(10)
            )

            sponsorTextView.layoutToTopLeft(
                sponsoredByTextView.right + convertDpToPx(5),
                sponsoredByTextView.top
            )

            informationTextView.layoutToTopRight(
                sponsorBackgroundView.right - convertDpToPx(15),
                sponsoredByTextView.top
            )

            sponsorBackgroundView.bottom

        } else {

            separatorView.bottom
        }

        val descriptionViewBottom = if (descriptionTextView.isVisible) {

            descriptionTextView.layoutToTopLeft(
                titleTextView.left,
                sponsorViewBottom + convertDpToPx(15)
            )

            descriptionTextView.bottom

        } else {

            sponsorViewBottom
        }

        goToTextView.layoutToTopLeft(
            titleTextView.left,
            descriptionViewBottom + convertDpToPx(15)
        )

        locationBackgroundView.layoutToTopLeft(
            (viewWidth - locationBackgroundView.measuredWidth) / 2,
            goToTextView.bottom + convertDpToPx(10)
        )

        retailerTextView.layoutToTopLeft(
            locationBackgroundView.left + convertDpToPx(15),
            locationBackgroundView.top + convertDpToPx(10)
        )

        addressTextView.layoutToTopLeft(
            retailerTextView.left,
            retailerTextView.bottom + convertDpToPx(10)
        )

        phoneImageView.layoutToTopLeft(
            retailerTextView.left,
            addressTextView.bottom + convertDpToPx(15)
        )

        phoneTextView.layoutToTopLeft(
            phoneImageView.right + convertDpToPx(5),
            phoneImageView.top + (phoneImageView.measuredHeight - phoneTextView.measuredHeight) / 2
        )

        if (mailImageView.isVisible) {
            mailImageView.layoutToTopLeft(
                phoneTextView.right + convertDpToPx(10),
                phoneImageView.top
            )

            mailTextView.layoutToTopLeft(
                mailImageView.right + convertDpToPx(5),
                mailImageView.top + (mailImageView.measuredHeight - mailTextView.measuredHeight) / 2
            )
        }

        openLocationLayout.layoutToTopLeft(
            (viewWidth - openLocationLayout.measuredWidth) / 2,
            locationBackgroundView.bottom - openLocationLayout.measuredHeight / 2
        )
    }
}