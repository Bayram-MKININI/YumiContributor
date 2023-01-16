package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent

class ManagedAccountView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private lateinit var availableVouchersTextView: TextView
    lateinit var availableCategoriesRecyclerView: RecyclerView
    lateinit var usedVouchersTextView: TextView
    lateinit var usedCategoriesRecyclerView: RecyclerView

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {
        availableVouchersTextView = findViewById(R.id.available_vouchers_text_view)
        availableCategoriesRecyclerView = findViewById(R.id.available_categories_recycler_view)
        usedVouchersTextView = findViewById(R.id.used_vouchers_text_view)
        usedCategoriesRecyclerView = findViewById(R.id.used_categories_recycler_view)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)

        availableVouchersTextView.measureWrapContent()

        availableCategoriesRecyclerView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        if (usedVouchersTextView.isVisible) {
            usedVouchersTextView.measureWrapContent()
        }

        if (usedCategoriesRecyclerView.isVisible) {
            usedCategoriesRecyclerView.measure(
                MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
        }

        val viewHeight =
            availableVouchersTextView.measuredHeight + availableCategoriesRecyclerView.measuredHeight +
                    convertDpToPx(10) +
                    if (usedVouchersTextView.isVisible) {
                        usedVouchersTextView.measuredHeight + usedCategoriesRecyclerView.measuredHeight + convertDpToPx(
                            20
                        )
                    } else
                        0

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        availableVouchersTextView.layoutToTopLeft(
            convertDpToPx(20),
            0
        )

        availableCategoriesRecyclerView.layoutToTopLeft(
            0,
            availableVouchersTextView.bottom + convertDpToPx(10)
        )

        if (usedVouchersTextView.isVisible) {
            usedVouchersTextView.layoutToTopLeft(
                convertDpToPx(20),
                availableCategoriesRecyclerView.bottom + convertDpToPx(10)
            )

            usedCategoriesRecyclerView.layoutToTopLeft(
                0,
                usedVouchersTextView.bottom + convertDpToPx(10)
            )
        }
    }
}