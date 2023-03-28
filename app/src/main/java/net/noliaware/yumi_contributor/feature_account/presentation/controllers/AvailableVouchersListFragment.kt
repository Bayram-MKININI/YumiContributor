package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore.Video.VideoColumns.CATEGORY
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.VOUCHER_DETAILS_FRAGMENT_TAG
import net.noliaware.yumi_contributor.commun.presentation.adapters.ListLoadStateAdapter
import net.noliaware.yumi_contributor.commun.util.handlePaginationError
import net.noliaware.yumi_contributor.commun.util.withArgs
import net.noliaware.yumi_contributor.feature_account.domain.model.Category
import net.noliaware.yumi_contributor.feature_account.presentation.adapters.VoucherAdapter
import net.noliaware.yumi_contributor.feature_account.presentation.mappers.AvailableVoucherMapper
import net.noliaware.yumi_contributor.feature_account.presentation.views.VouchersListView
import net.noliaware.yumi_contributor.feature_account.presentation.views.VouchersListView.*

@AndroidEntryPoint
class AvailableVouchersListFragment : AppCompatDialogFragment() {

    companion object {
        fun newInstance(category: Category) =
            AvailableVouchersListFragment().withArgs(CATEGORY to category)
    }

    private var vouchersListView: VouchersListView? = null
    private val viewModel by viewModels<AvailableVouchersListFragmentViewModel>()
    var onDataRefreshed: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.vouchers_list_layout, container, false).apply {
            vouchersListView = this as VouchersListView
            vouchersListView?.callback = readMailViewCallback
            vouchersListView?.voucherAdapter = VoucherAdapter(
                color = viewModel.selectedCategory?.categoryColor ?: Color.TRANSPARENT,
                voucherMapper = AvailableVoucherMapper()
            ) { voucher ->
                VoucherDetailsFragment.newInstance(
                    categoryUI = CategoryUI(
                        categoryColor = viewModel.selectedCategory?.categoryColor,
                        categoryIcon = viewModel.selectedCategory?.categoryIcon,
                        categoryLabel = viewModel.selectedCategory?.categoryLabel
                    ),
                    voucherId = voucher.voucherId
                ).apply {
                    this.onDataRefreshed = {
                        viewModel.dataShouldRefresh = true
                        vouchersListView?.voucherAdapter?.refresh()
                    }
                }.show(
                    childFragmentManager.beginTransaction(),
                    VOUCHER_DETAILS_FRAGMENT_TAG
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title = getString(
            R.string.vouchers_list,
            viewModel.selectedCategory?.categoryLabel.orEmpty()
        )
        vouchersListView?.fillViewWithData(
            VouchersListViewAdapter(
                title = decorateText(
                    text = title,
                    coloredText = viewModel.selectedCategory?.categoryLabel.orEmpty(),
                    color = viewModel.selectedCategory?.categoryColor ?: Color.TRANSPARENT
                ),
                color = viewModel.selectedCategory?.categoryColor ?: Color.TRANSPARENT,
                iconName = viewModel.selectedCategory?.categoryIcon
            )
        )
        collectFlows()
    }

    private fun decorateText(
        text: String,
        coloredText: String,
        color: Int
    ) = SpannableString(text).apply {
        val colorSpan = ForegroundColorSpan(color)
        val startIndex = text.indexOf(coloredText)
        val endIndex = text.length
        setSpan(
            colorSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vouchersListView?.voucherAdapter?.loadStateFlow?.collectLatest { loadState ->
                handlePaginationError(loadState)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getVouchers()?.collectLatest {
                vouchersListView?.voucherAdapter?.withLoadStateFooter(
                    footer = ListLoadStateAdapter()
                )
                vouchersListView?.voucherAdapter?.submitData(it)
            }
        }
    }

    private val readMailViewCallback: VouchersListViewCallback by lazy {
        VouchersListViewCallback {
            dismissAllowingStateLoss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (viewModel.dataShouldRefresh == true) {
            onDataRefreshed?.invoke()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        vouchersListView = null
    }
}