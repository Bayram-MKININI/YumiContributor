package net.noliaware.yumi_contributor.feature_login.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.GOLDEN_RATIO
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.find
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.feature_login.presentation.views.AccountCategoryView.AccountCategoryViewAdapter
import kotlin.math.roundToInt

class AccountItemView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private val titleTextView by lazy { find<TextView>(R.id.title_text_view) }
    private val phoneTextView by lazy { find<TextView>(R.id.phone_text_view) }
    private val lastLoginTextView by lazy { find<TextView>(R.id.last_login_text_view) }
    private val separatorLineView by lazy { find<View>(R.id.separator_line_view) }
    private val categoriesLinearLayout by lazy { find<LinearLayoutCompat>(R.id.categories_linear_layout) }

    data class AccountItemViewAdapter(
        val title: String = "",
        val phoneNumber: String = "",
        val lastLogin: String = "",
        val accountCategoryViewAdapters: MutableList<AccountCategoryViewAdapter> = mutableListOf()
    )

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    fun fillViewWithData(accountItemViewAdapter: AccountItemViewAdapter) {

        titleTextView.text = accountItemViewAdapter.title
        phoneTextView.text = accountItemViewAdapter.phoneNumber
        lastLoginTextView.text = accountItemViewAdapter.lastLogin

        accountItemViewAdapter.accountCategoryViewAdapters.forEach { accountCategoryViewAdapter ->

            AccountCategoryView(context).also {
                it.fillViewWithData(accountCategoryViewAdapter)
                categoriesLinearLayout.addView(it)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        var viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        titleTextView.measureWrapContent()

        phoneTextView.measureWrapContent()

        lastLoginTextView.measureWrapContent()

        separatorLineView.measure(
            MeasureSpec.makeMeasureSpec(viewWidth * 8 / 10, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(1), MeasureSpec.EXACTLY)
        )

        categoriesLinearLayout.measure(
            MeasureSpec.makeMeasureSpec(
                (viewWidth / GOLDEN_RATIO).roundToInt(),
                MeasureSpec.EXACTLY
            ),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        viewHeight =
            titleTextView.measuredHeight + phoneTextView.measuredHeight + lastLoginTextView.measuredHeight + separatorLineView.measuredHeight + categoriesLinearLayout.measuredHeight + convertDpToPx(
                70
            )

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        titleTextView.layoutToTopLeft(
            (viewWidth - titleTextView.measuredWidth) / 2,
            convertDpToPx(15)
        )

        phoneTextView.layoutToTopLeft(
            (viewWidth - phoneTextView.measuredWidth) / 2,
            titleTextView.bottom + convertDpToPx(10)
        )

        lastLoginTextView.layoutToTopLeft(
            (viewWidth - lastLoginTextView.measuredWidth) / 2,
            phoneTextView.bottom + convertDpToPx(10)
        )

        separatorLineView.layoutToTopLeft(
            (viewWidth - separatorLineView.measuredWidth) / 2,
            lastLoginTextView.bottom + convertDpToPx(15)
        )

        categoriesLinearLayout.layoutToTopLeft(
            (viewWidth - categoriesLinearLayout.measuredWidth) / 2,
            separatorLineView.bottom + convertDpToPx(15)
        )
    }
}