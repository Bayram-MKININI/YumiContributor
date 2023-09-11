package net.noliaware.yumi_contributor.feature_account.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.adapters.BaseAdapter
import net.noliaware.yumi_contributor.commun.util.convertDpToPx
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.layoutToTopLeft
import net.noliaware.yumi_contributor.commun.util.layoutToTopRight
import net.noliaware.yumi_contributor.commun.util.measureWrapContent
import net.noliaware.yumi_contributor.commun.util.weak
import net.noliaware.yumi_contributor.feature_account.presentation.adapters.FilteredManagedAccountsAdapter
import net.noliaware.yumi_contributor.feature_account.presentation.adapters.PaginatedManagedAccountsAdapter

class AccountsListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewGroup(context, attrs, defStyle) {

    private lateinit var searchAutoCompleteLayout: View
    private lateinit var searchAutoComplete: AutoCompleteTextView
    private lateinit var closeImageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var contentView: View
    private lateinit var shimmerView: ShimmerFrameLayout
    private lateinit var shimmerRecyclerView: RecyclerView
    private lateinit var accountsRecyclerView: RecyclerView
    private lateinit var filteredAccountRecyclerView: RecyclerView
    var callback: AccountsListViewCallback? by weak()

    var paginatedManagedAccountsAdapter
        get() = accountsRecyclerView.adapter as PaginatedManagedAccountsAdapter
        set(adapter) {
            accountsRecyclerView.adapter = adapter
        }

    var filteredManagedAccountsAdapter
        get() = filteredAccountRecyclerView.adapter as FilteredManagedAccountsAdapter
        set(adapter) {
            filteredAccountRecyclerView.adapter = adapter
        }

    interface AccountsListViewCallback {
        fun onSuggestionSelected(id: String)
        fun onSuggestedAccountSelected()
        fun onSuggestionCleared()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun initView() {

        searchAutoCompleteLayout = findViewById(R.id.search_auto_complete_layout)
        searchAutoComplete = searchAutoCompleteLayout.findViewById(R.id.search_auto_complete)
        searchAutoComplete.setOnItemClickListener { parent, _, position, _ ->
            val suggestion: String = parent.getItemAtPosition(position) as String
            callback?.onSuggestionSelected(
                extractIdFromSuggestion(suggestion)
            )
        }
        searchAutoComplete.doAfterTextChanged {
            if (searchAutoComplete.text.isNullOrEmpty()) {
                callback?.onSuggestionCleared()
            }
        }
        closeImageView = searchAutoCompleteLayout.findViewById(R.id.close_image_view)
        closeImageView.setOnClickListener { searchAutoComplete.text.clear() }

        titleTextView = findViewById(R.id.title_text_view)

        contentView = findViewById(R.id.content_layout)

        shimmerView = findViewById(R.id.shimmer_view)
        shimmerRecyclerView = shimmerView.findViewById(R.id.shimmer_recycler_view)
        setUpRecyclerView(shimmerRecyclerView)
        BaseAdapter((0..1).map { 0 }).apply {
            expressionOnCreateViewHolder = { viewGroup ->
                viewGroup.inflate(R.layout.account_item_placeholder_layout)
            }
            shimmerRecyclerView.adapter = this
        }

        accountsRecyclerView = contentView.findViewById(R.id.accounts_recycler_view)
        setUpRecyclerView(accountsRecyclerView)

        filteredAccountRecyclerView = contentView.findViewById(R.id.filtered_accounts_recycler_view)
        setUpRecyclerView(filteredAccountRecyclerView)
    }

    private fun setUpRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun extractIdFromSuggestion(suggestion: String): String {
        val pattern = Regex("\\((.*?)\\)")
        val results = pattern.findAll(suggestion)
        return results.map {
            it.groupValues[1]
        }.joinToString()
    }

    fun setLoadingVisible(visible: Boolean) {
        if (visible) {
            shimmerView.isVisible = true
            shimmerView.startShimmer()
        } else {
            shimmerView.isGone = true
            shimmerView.stopShimmer()
        }
    }

    fun setUpSearchAutoComplete(suggestions: List<String>) {
        val adapter = ArrayAdapter(context, R.layout.autocomplete_suggestion_layout, suggestions)
        searchAutoComplete.setAdapter(adapter)
    }

    fun displayAllAccount() {
        accountsRecyclerView.isVisible = true
        filteredAccountRecyclerView.isGone = true
    }

    fun displayFilteredAccount() {
        accountsRecyclerView.isGone = true
        filteredAccountRecyclerView.isVisible = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)

        closeImageView.measureWrapContent()

        val searchAutoCompleteLayoutWidth = viewWidth * 85 / 100
        val searchAutoCompleteWidth = searchAutoCompleteLayoutWidth - (closeImageView.measuredWidth
                + convertDpToPx(30))
        searchAutoComplete.measure(
            MeasureSpec.makeMeasureSpec(searchAutoCompleteWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(40), MeasureSpec.EXACTLY)
        )

        searchAutoCompleteLayout.measure(
            MeasureSpec.makeMeasureSpec(searchAutoCompleteLayoutWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(convertDpToPx(40), MeasureSpec.EXACTLY)
        )

        titleTextView.measureWrapContent()

        val contentViewWidth = viewWidth * 95 / 100
        val sideMargin = viewWidth * 5 / 100 / 2
        val contentViewHeight = viewHeight - (searchAutoCompleteLayout.measuredHeight + titleTextView.measuredHeight
                    + sideMargin + convertDpToPx(70))

        contentView.measure(
            MeasureSpec.makeMeasureSpec(contentViewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(contentViewHeight, MeasureSpec.EXACTLY)
        )

        if (shimmerView.isVisible) {
            shimmerView.measure(
                MeasureSpec.makeMeasureSpec(contentViewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
        }

        if (accountsRecyclerView.isVisible) {
            accountsRecyclerView.measure(
                MeasureSpec.makeMeasureSpec(contentViewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(contentViewHeight, MeasureSpec.EXACTLY)
            )
        }

        if (filteredAccountRecyclerView.isVisible) {
            filteredAccountRecyclerView.measure(
                MeasureSpec.makeMeasureSpec(contentViewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(contentViewHeight, MeasureSpec.EXACTLY)
            )
        }

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val viewWidth = right - left
        val viewHeight = bottom - top

        searchAutoCompleteLayout.layoutToTopLeft(
            (viewWidth - searchAutoCompleteLayout.measuredWidth) / 2,
            convertDpToPx(20)
        )

        searchAutoComplete.layoutToTopLeft(0, 0)

        closeImageView.layoutToTopRight(
            searchAutoCompleteLayout.width - convertDpToPx(15),
            (searchAutoCompleteLayout.measuredHeight - closeImageView.measuredHeight) / 2
        )

        titleTextView.layoutToTopLeft(
            searchAutoCompleteLayout.left,
            searchAutoCompleteLayout.bottom + convertDpToPx(15)
        )

        contentView.layoutToTopLeft(
            (viewWidth - contentView.measuredWidth) / 2,
            titleTextView.bottom + convertDpToPx(15)
        )

        if (shimmerView.isVisible) {
            shimmerView.layoutToTopLeft(0, 0)
        }

        if (accountsRecyclerView.isVisible) {
            accountsRecyclerView.layoutToTopLeft(0, 0)
        }

        if (filteredAccountRecyclerView.isVisible) {
            filteredAccountRecyclerView.layoutToTopLeft(0, 0)
        }
    }
}