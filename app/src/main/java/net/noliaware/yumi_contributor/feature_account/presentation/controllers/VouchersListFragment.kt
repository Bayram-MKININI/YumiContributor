package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.content.DialogInterface
import android.os.Bundle
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
import net.noliaware.yumi_contributor.commun.CATEGORY_ID
import net.noliaware.yumi_contributor.commun.CATEGORY_LABEL
import net.noliaware.yumi_contributor.commun.VOUCHER_DETAILS_FRAGMENT_TAG
import net.noliaware.yumi_contributor.commun.presentation.adapters.ListLoadStateAdapter
import net.noliaware.yumi_contributor.commun.util.handlePaginationError
import net.noliaware.yumi_contributor.commun.util.withArgs
import net.noliaware.yumi_contributor.feature_account.presentation.views.VouchersListView

@AndroidEntryPoint
class VouchersListFragment : AppCompatDialogFragment() {

    companion object {
        fun newInstance(categoryId: String, categoryLabel: String) =
            VouchersListFragment().withArgs(
                CATEGORY_ID to categoryId,
                CATEGORY_LABEL to categoryLabel
            )
    }

    private var vouchersListView: VouchersListView? = null
    private val viewModel by viewModels<VouchersListFragmentViewModel>()
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
            vouchersListView?.voucherAdapter =
                net.noliaware.yumi_contributor.feature_account.presentation.adapters.VoucherAdapter(
                    AvailableVoucherMapper()
                ) { voucher ->
                    VoucherDetailsFragment.newInstance(
                        voucher.voucherId
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
        vouchersListView?.setTitle(viewModel.categoryLabel)
        collectFlows()
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            vouchersListView?.voucherAdapter?.loadStateFlow?.collectLatest { loadState ->
                handlePaginationError(loadState)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.getVouchers().collectLatest {
                vouchersListView?.voucherAdapter?.withLoadStateFooter(
                    footer = ListLoadStateAdapter()
                )
                vouchersListView?.voucherAdapter?.submitData(it)
            }
        }
    }

    private val readMailViewCallback: VouchersListView.VouchersListViewCallback by lazy {
        object : VouchersListView.VouchersListViewCallback {
            override fun onBackButtonClicked() {
                dismissAllowingStateLoss()
            }
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