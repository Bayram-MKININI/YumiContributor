package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.views.FillableTextWidget
import net.noliaware.yumi_contributor.commun.util.MarginItemDecoration
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.getColorCompat
import net.noliaware.yumi_contributor.commun.util.getFontFromResources
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.sizeForVisible
import net.noliaware.yumi_contributor.feature_account.presentation.views.VoucherDetailsContainerView.VoucherDetailsViewAdapter

class VoucherDetailsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var titleFillableTextWidget: FillableTextWidget
    private lateinit var crossOutView: View

    lateinit var requestSpinner: Spinner
        private set

    private lateinit var dateFillableTextWidget: FillableTextWidget
    private lateinit var voucherNumberFillableTextWidget: FillableTextWidget
    lateinit var ongoingRequestsButton: View
    private lateinit var separatorView: View
    lateinit var partnersRecyclerView: RecyclerView
    private lateinit var descriptionTextView: TextView
    lateinit var moreTextView: TextView
    private lateinit var goToTextView: TextView
    private lateinit var locationBackgroundView: View
    private lateinit var retailerTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var retrievalTextView: TextView
    lateinit var phoneButton: View
    private lateinit var mailButton: View
    lateinit var openLocationLayout: LinearLayoutCompat
    private var displayVoucherButtonVisible: Boolean = false
    private var statusTextViewVisible: Boolean = false

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        titleFillableTextWidget = findViewById(R.id.title_fillable_text_view)
        titleFillableTextWidget.textView.apply {
            typeface = context.getFontFromResources(R.font.omnes_semibold_regular)
            setTextColor(context.getColorCompat(R.color.grey_2))
            textSize = 19f
        }
        titleFillableTextWidget.setFixedWidth(true)

        crossOutView = findViewById(R.id.cross_out_view)

        requestSpinner = findViewById(R.id.request_spinner)

        voucherNumberFillableTextWidget = findViewById(R.id.voucher_number_fillable_text_view)
        voucherNumberFillableTextWidget.textView.apply {
            typeface = context.getFontFromResources(R.font.omnes_light)
            setTextColor(context.getColorCompat(R.color.grey_2))
            textSize = 13f
        }

        dateFillableTextWidget = findViewById(R.id.date_fillable_text_view)
        dateFillableTextWidget.textView.apply {
            typeface = context.getFontFromResources(R.font.omnes_light)
            setTextColor(context.getColorCompat(R.color.grey_2))
            textSize = 13f
        }

        ongoingRequestsButton = findViewById(R.id.ongoing_requests_action_layout)
        separatorView = findViewById(R.id.separator_view)
        partnersRecyclerView = findViewById(R.id.partners_recycler_view)
        partnersRecyclerView.setUp()
        descriptionTextView = findViewById(R.id.description_text_view)
        moreTextView = findViewById(R.id.more_text_view)
        goToTextView = findViewById(R.id.go_to_text_view)
        locationBackgroundView = findViewById(R.id.location_background)
        retailerTextView = findViewById(R.id.retailer_text_view)
        addressTextView = findViewById(R.id.address_text_view)
        retrievalTextView = findViewById(R.id.retrieval_text_view)
        phoneButton = findViewById(R.id.phone_action_layout)
        mailButton = findViewById(R.id.mail_action_layout)
        openLocationLayout = findViewById(R.id.open_location_layout)
    }

    private fun RecyclerView.setUp() {
        layoutManager = LinearLayoutManager(context)
        addItemDecoration(MarginItemDecoration(convertDpToPx(20)))
    }

    fun fillViewWithData(voucherDetailsViewAdapter: VoucherDetailsViewAdapter) {

        titleFillableTextWidget.setText(voucherDetailsViewAdapter.title)
        crossOutView.isVisible = voucherDetailsViewAdapter.titleCrossed
        requestSpinner.isVisible = voucherDetailsViewAdapter.requestsAvailable
        voucherNumberFillableTextWidget.setText(voucherDetailsViewAdapter.voucherNumber)
        dateFillableTextWidget.setText(voucherDetailsViewAdapter.date)
        ongoingRequestsButton.isVisible = voucherDetailsViewAdapter.ongoingRequestsAvailable

        if (voucherDetailsViewAdapter.partnersAvailable) {
            partnersRecyclerView.isVisible = true
        }

        voucherDetailsViewAdapter.voucherDescription?.let {
            descriptionTextView.isVisible = true
            descriptionTextView.text = voucherDetailsViewAdapter.voucherDescription
        }

        if (voucherDetailsViewAdapter.moreActionAvailable) {
            moreTextView.isVisible = true
        }

        retailerTextView.text = voucherDetailsViewAdapter.retailerLabel
        addressTextView.text = voucherDetailsViewAdapter.retailerAddress

        voucherDetailsViewAdapter.retrievalMode?.let {
            retrievalTextView.isVisible = true
            retrievalTextView.text = voucherDetailsViewAdapter.retrievalMode
        }

        displayVoucherButtonVisible = !voucherDetailsViewAdapter.voucherStatusAvailable
        statusTextViewVisible = voucherDetailsViewAdapter.voucherStatusAvailable
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)

        if (requestSpinner.isVisible) {
            requestSpinner.measure(
                MeasureSpec.makeMeasureSpec(convertDpToPx(30), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(convertDpToPx(40), MeasureSpec.EXACTLY)
            )
        }

        val titleWidth = viewWidth - convertDpToPx(40) -
                requestSpinner.sizeForVisible { requestSpinner.measuredWidth }

        titleFillableTextWidget.measure(
            MeasureSpec.makeMeasureSpec(titleWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(22), MeasureSpec.EXACTLY)
        )

        if (crossOutView.isVisible) {
            crossOutView.measure(
                MeasureSpec.makeMeasureSpec(
                    titleFillableTextWidget.measuredWidth * 105 / 100,
                    MeasureSpec.EXACTLY
                ),
                MeasureSpec.makeMeasureSpec(convertDpToPx(3), MeasureSpec.EXACTLY)
            )
        }

        voucherNumberFillableTextWidget.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 7 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(15), MeasureSpec.EXACTLY)
        )

        dateFillableTextWidget.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 5 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(15), MeasureSpec.EXACTLY)
        )

        if (ongoingRequestsButton.isVisible) {
            ongoingRequestsButton.measureWrapContent()
        }

        separatorView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 4 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(3), MeasureSpec.EXACTLY)
        )

        if (partnersRecyclerView.isVisible) {
            partnersRecyclerView.measure(
                MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
        }

        if (descriptionTextView.isVisible) {
            descriptionTextView.measure(
                MeasureSpec.makeMeasureSpec(viewWidth * 9 / 10, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
        }

        if (moreTextView.isVisible) {
            moreTextView.measureWrapContent()
        }

        goToTextView.measureWrapContent()

        retailerTextView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 8 / 10, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        addressTextView.measureWrapContent()

        if (retrievalTextView.isVisible) {
            retrievalTextView.measure(
                MeasureSpec.makeMeasureSpec(viewWidth * 8 / 10, MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
        }

        phoneButton.measureWrapContent()

        if (mailButton.isVisible) {
            mailButton.measureWrapContent()
        }

        openLocationLayout.measureWrapContent()

        val locationBackgroundViewHeight = retailerTextView.measuredHeight + addressTextView.measuredHeight +
                retrievalTextView.sizeForVisible {
                    retrievalTextView.measuredHeight + convertDpToPx(10)
                } +
                phoneButton.measuredHeight + openLocationLayout.measuredHeight / 2 + convertDpToPx(50)

        locationBackgroundView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 9 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(locationBackgroundViewHeight, MeasureSpec.EXACTLY)
        )

        val contentMeasuredHeight = titleFillableTextWidget.measuredHeight + dateFillableTextWidget.measuredHeight +
                voucherNumberFillableTextWidget.measuredHeight +
                ongoingRequestsButton.sizeForVisible {
                    ongoingRequestsButton.measuredHeight + convertDpToPx(10)
                } + separatorView.measuredHeight + partnersRecyclerView.measuredHeight +
                descriptionTextView.sizeForVisible {
                    descriptionTextView.measuredHeight + convertDpToPx(15)
                } +
                moreTextView.sizeForVisible {
                    moreTextView.measuredHeight + convertDpToPx(10)
                } +
                goToTextView.measuredHeight + locationBackgroundView.measuredHeight +
                openLocationLayout.measuredHeight / 2 + convertDpToPx(65)

        val finalViewHeight = when {
            displayVoucherButtonVisible -> contentMeasuredHeight + convertDpToPx(70)
            statusTextViewVisible -> contentMeasuredHeight + convertDpToPx(60)
            else -> contentMeasuredHeight
        }

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(finalViewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        val viewWidth = right - left
        val viewHeight = bottom - top

        titleFillableTextWidget.layoutToTopLeft(
            convertDpToPx(20),
            0
        )

        if (crossOutView.isVisible) {
            crossOutView.layoutToTopLeft(
                titleFillableTextWidget.left + (titleFillableTextWidget.measuredWidth - crossOutView.measuredWidth) / 2,
                titleFillableTextWidget.top + (titleFillableTextWidget.measuredHeight - crossOutView.measuredHeight) / 2
            )
        }

        if (requestSpinner.isVisible) {
            requestSpinner.layoutToTopRight(
                viewWidth - convertDpToPx(20),
                titleFillableTextWidget.top + (titleFillableTextWidget.measuredHeight - requestSpinner.measuredHeight) / 2
            )
        }

        voucherNumberFillableTextWidget.layoutToTopLeft(
            titleFillableTextWidget.left,
            titleFillableTextWidget.bottom + convertDpToPx(10)
        )

        dateFillableTextWidget.layoutToTopLeft(
            titleFillableTextWidget.left,
            voucherNumberFillableTextWidget.bottom + convertDpToPx(10)
        )

        val separatorCeil = if (ongoingRequestsButton.isVisible) {
            ongoingRequestsButton.layoutToTopLeft(
                titleFillableTextWidget.left,
                dateFillableTextWidget.bottom + convertDpToPx(10)
            )
            ongoingRequestsButton.bottom
        } else {
            dateFillableTextWidget.bottom
        }

        separatorView.layoutToTopLeft(
            (viewWidth - separatorView.measuredWidth) / 2,
            separatorCeil + convertDpToPx(15)
        )

        val partnerViewBottom = if (partnersRecyclerView.isVisible) {
            partnersRecyclerView.layoutToTopLeft(
                (viewWidth - partnersRecyclerView.measuredWidth) / 2,
                separatorView.bottom
            )
            partnersRecyclerView.bottom - convertDpToPx(15)
        } else {
            separatorView.bottom
        }

        val descriptionViewBottom = if (descriptionTextView.isVisible) {
            descriptionTextView.layoutToTopLeft(
                titleFillableTextWidget.left,
                partnerViewBottom + convertDpToPx(15)
            )
            descriptionTextView.bottom
        } else {
            partnerViewBottom
        }

        val moreViewBottom = if (moreTextView.isVisible) {
            val edgeSpace = viewWidth * 5 / 100
            moreTextView.layoutToTopRight(
                viewWidth - edgeSpace,
                descriptionViewBottom + convertDpToPx(10)
            )
            moreTextView.bottom
        } else {
            descriptionViewBottom
        }

        goToTextView.layoutToTopLeft(
            titleFillableTextWidget.left,
            moreViewBottom + convertDpToPx(15)
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

        val retrievalTextViewBottom = if (retrievalTextView.isVisible) {
            retrievalTextView.layoutToTopLeft(
                retailerTextView.left,
                addressTextView.bottom + convertDpToPx(10)
            )
            retrievalTextView.bottom
        } else {
            addressTextView.bottom
        }

        phoneButton.layoutToTopLeft(
            retailerTextView.left,
            retrievalTextViewBottom + convertDpToPx(15)
        )

        if (mailButton.isVisible) {
            mailButton.layoutToTopLeft(
                phoneButton.right + convertDpToPx(10),
                phoneButton.top
            )
        }

        openLocationLayout.layoutToTopLeft(
            (viewWidth - openLocationLayout.measuredWidth) / 2,
            locationBackgroundView.bottom - openLocationLayout.measuredHeight / 2
        )
    }
}