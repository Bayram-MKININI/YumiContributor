package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.text.SpannableString
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.facebook.shimmer.ShimmerFrameLayout
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.activateShimmer
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.drawableIdByName
import net.noliaware.yumi_contributor.commun.util.getDrawableCompat
import net.noliaware.yumi_contributor.commun.util.getStatusBarHeight
import net.noliaware.yumi_contributor.commun.util.layoutToBottomLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.tint
import net.noliaware.yumi_contributor.feature_account.presentation.adapters.VoucherPartnersAdapter

class VoucherDetailsContainerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var backgroundView: View
    private lateinit var headerView: View
    private lateinit var backView: View
    private lateinit var categoryImageView: ImageView
    private lateinit var parentContentView: View
    private lateinit var shimmerView: ShimmerFrameLayout
    private lateinit var voucherDetailsView: VoucherDetailsView
    var voucherPartnersAdapter
        get() = voucherDetailsView.partnersRecyclerView.adapter as VoucherPartnersAdapter
        set(adapter) {
            voucherDetailsView.partnersRecyclerView.adapter = adapter
        }
    private lateinit var displayVoucherLayout: LinearLayoutCompat
    private lateinit var voucherStatusTextView: TextView
    var callback: VoucherDetailsViewCallback? = null

    val getRequestSpinner get() = voucherDetailsView.requestSpinner

    data class VoucherDetailsViewAdapter(
        val title: String = "",
        val titleCrossed: Boolean = false,
        val requestsAvailable: Boolean = false,
        val voucherNumber: SpannableString?,
        val date: SpannableString?,
        val ongoingRequestsAvailable: Boolean,
        val partnersAvailable: Boolean,
        val voucherDescription: String? = null,
        val moreActionAvailable: Boolean,
        val retailerLabel: String = "",
        val retailerAddress: String = "",
        val retrievalMode: String? = null,
        val voucherStatusAvailable: Boolean = false,
        val voucherStatus: String = ""
    )

    interface VoucherDetailsViewCallback {
        fun onBackButtonClicked()
        fun onRequestSelectedAtIndex(index: Int)
        fun onOngoingRequestsClicked()
        fun onMoreButtonClicked()
        fun onPhoneButtonClicked()
        fun onLocationClicked()
        fun onDisplayVoucherButtonClicked()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        backgroundView = findViewById(R.id.background_view)
        headerView = findViewById(R.id.header_view)
        backView = findViewById(R.id.back_view)
        backView.setOnClickListener(onButtonClickListener)
        categoryImageView = findViewById(R.id.category_image_view)

        parentContentView = findViewById(R.id.parent_content_layout)
        shimmerView = findViewById(R.id.shimmer_view)
        voucherDetailsView = shimmerView.findViewById(R.id.content_layout)
        post {
            voucherDetailsView.requestSpinner.setSelection(
                voucherDetailsView.requestSpinner.adapter.count,
                false
            )
            voucherDetailsView.requestSpinner.onItemSelectedListener = onSpinnerItemSelectedListener
        }
        voucherDetailsView.ongoingRequestsButton.setOnClickListener(onButtonClickListener)
        voucherDetailsView.moreTextView.setOnClickListener(onButtonClickListener)
        voucherDetailsView.openLocationLayout.setOnClickListener(onButtonClickListener)
        voucherDetailsView.phoneButton.setOnClickListener(onButtonClickListener)

        displayVoucherLayout = parentContentView.findViewById(R.id.display_voucher_layout)
        displayVoucherLayout.setOnClickListener(onButtonClickListener)

        voucherStatusTextView = parentContentView.findViewById(R.id.voucher_status_text_view)
    }

    private val onButtonClickListener: OnClickListener by lazy {
        OnClickListener {
            when (it.id) {
                R.id.back_view -> callback?.onBackButtonClicked()
                R.id.ongoing_requests_action_layout -> callback?.onOngoingRequestsClicked()
                R.id.more_text_view -> callback?.onMoreButtonClicked()
                R.id.phone_action_layout -> callback?.onPhoneButtonClicked()
                R.id.open_location_layout -> callback?.onLocationClicked()
                R.id.display_voucher_layout -> callback?.onDisplayVoucherButtonClicked()
            }
        }
    }

    private val onSpinnerItemSelectedListener: AdapterView.OnItemSelectedListener by lazy {
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val lastPosition = parent?.count ?: 0
                if (position < lastPosition) {
                    callback?.onRequestSelectedAtIndex(position)
                    voucherDetailsView.requestSpinner.setSelection(lastPosition, false)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    fun activateLoading(visible: Boolean) {
        shimmerView.activateShimmer(visible)
    }

    fun setUpViewLook(color: Int, iconName: String?) {
        headerView.setBackgroundColor(color)
        categoryImageView.setImageResource(context.drawableIdByName(iconName))
        setUpPrimaryColor(color)
    }

    private fun setUpPrimaryColor(color: Int) {
        displayVoucherLayout.background = context.getDrawableCompat(
            R.drawable.rectangle_rounded_22dp
        )?.tint(color)
    }

    fun fillViewWithData(voucherDetailsViewAdapter: VoucherDetailsViewAdapter) {
        voucherDetailsView.fillViewWithData(voucherDetailsViewAdapter)

        if (voucherDetailsViewAdapter.voucherStatusAvailable) {
            displayVoucherLayout.isGone = true
            voucherStatusTextView.isVisible = true
            voucherStatusTextView.text = voucherDetailsViewAdapter.voucherStatus
        } else {
            displayVoucherLayout.isVisible = true
            voucherStatusTextView.isGone = true
        }
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
                getStatusBarHeight() + convertDpToPx(92),
                MeasureSpec.EXACTLY
            )
        )

        backView.measureWrapContent()

        categoryImageView.measure(
            MeasureSpec.makeMeasureSpec(convertDpToPx(86), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(86), MeasureSpec.EXACTLY)
        )

        if (displayVoucherLayout.isVisible) {
            displayVoucherLayout.measureWrapContent()
        }
        if (voucherStatusTextView.isVisible) {
            voucherStatusTextView.measureWrapContent()
        }

        val parentContentViewHeight = viewHeight -
                (headerView.measuredHeight + categoryImageView.measuredHeight / 2 + convertDpToPx(25))

        parentContentView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 95 / 100, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(parentContentViewHeight, MeasureSpec.EXACTLY)
        )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        val viewWidth = right - left
        val viewHeight = bottom - top

        backgroundView.layoutToBottomLeft(0, getStatusBarHeight())

        headerView.layoutToTopLeft(0, 0)

        backView.layoutToTopLeft(convertDpToPx(10), getStatusBarHeight() + convertDpToPx(10))

        categoryImageView.layoutToTopLeft(
            (viewWidth - categoryImageView.measuredWidth) / 2,
            headerView.bottom - categoryImageView.measuredHeight / 2
        )

        parentContentView.layoutToTopLeft(
            (viewWidth - parentContentView.measuredWidth) / 2,
            categoryImageView.bottom + convertDpToPx(15)
        )

        when {
            voucherStatusTextView.isVisible -> {
                voucherStatusTextView.layoutToBottomLeft(
                    (parentContentView.measuredWidth - voucherStatusTextView.measuredWidth) / 2,
                    parentContentView.height - convertDpToPx(40)
                )
            }
            displayVoucherLayout.isVisible -> {
                displayVoucherLayout.layoutToBottomLeft(
                    (parentContentView.measuredWidth - displayVoucherLayout.measuredWidth) / 2,
                    parentContentView.height - convertDpToPx(40)
                )
            }
        }
    }
}