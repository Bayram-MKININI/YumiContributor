package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.adapters.ListLoadStateAdapter
import net.noliaware.yumi_contributor.commun.util.DecoratedText
import net.noliaware.yumi_contributor.commun.util.decorateWords
import net.noliaware.yumi_contributor.commun.util.getColorCompat
import net.noliaware.yumi_contributor.commun.util.handlePaginationError
import net.noliaware.yumi_contributor.commun.util.navDismiss
import net.noliaware.yumi_contributor.commun.util.safeNavigate
import net.noliaware.yumi_contributor.feature_account.presentation.adapters.VoucherAdapter
import net.noliaware.yumi_contributor.feature_account.presentation.mappers.CancelledVoucherMapper
import net.noliaware.yumi_contributor.feature_account.presentation.views.VouchersListView
import net.noliaware.yumi_contributor.feature_account.presentation.views.VouchersListView.*

@AndroidEntryPoint
class CancelledVouchersListFragment : AppCompatDialogFragment() {

    private var vouchersListView: VouchersListView? = null
    private val args by navArgs<CancelledVouchersListFragmentArgs>()
    private val viewModel by viewModels<CancelledVouchersListFragmentViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.FullScreenDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(
        R.layout.vouchers_list_layout,
        container,
        false
    ).apply {
        vouchersListView = this as VouchersListView
        vouchersListView?.callback = vouchersListViewCallback
        vouchersListView?.voucherAdapter = VoucherAdapter(
            color = args.selectedCategory.categoryColor,
            voucherMapper = CancelledVoucherMapper()
        ) { voucher ->
            findNavController().safeNavigate(
                CancelledVouchersListFragmentDirections.actionCancelledVouchersListFragmentToVoucherDetailsFragment(
                    categoryUI = CategoryUI(
                        categoryColor = args.selectedCategory.categoryColor,
                        categoryIcon = args.selectedCategory.categoryIcon
                    ), voucherId = voucher.voucherId
                )
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewWithSelectedCategory()
        vouchersListView?.setLoadingVisible(true)
        collectFlows()
    }

    private fun setUpViewWithSelectedCategory() {
        val title = getString(
            R.string.cancelled_vouchers_list,
            args.selectedCategory.categoryLabel
        )
        vouchersListView?.fillViewWithData(
            VouchersListViewAdapter(
                color = args.selectedCategory.categoryColor,
                iconName = args.selectedCategory.categoryIcon,
                title = title.decorateWords(
                    wordsToDecorate = listOf(
                        DecoratedText(
                            textToDecorate = getString(R.string.cancelled).lowercase(),
                            color = context?.getColorCompat(R.color.colorPrimary)
                                ?: Color.TRANSPARENT
                        ),
                        DecoratedText(
                            textToDecorate = args.selectedCategory.categoryLabel,
                            color = args.selectedCategory.categoryColor
                        )
                    )
                )
            )
        )
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            vouchersListView?.voucherAdapter?.loadStateFlow?.collectLatest { loadState ->
                when {
                    handlePaginationError(loadState) -> vouchersListView?.stopLoading()
                    loadState.refresh is LoadState.NotLoading -> {
                        vouchersListView?.setLoadingVisible(false)
                    }
                    else -> Unit
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getVouchers()?.collectLatest {
                vouchersListView?.voucherAdapter?.withLoadStateFooter(
                    footer = ListLoadStateAdapter()
                )
                vouchersListView?.voucherAdapter?.submitData(it)
            }
        }
    }

    private val vouchersListViewCallback: VouchersListViewCallback by lazy {
        VouchersListViewCallback {
            navDismiss()
        }
    }

    override fun onDestroyView() {
        vouchersListView?.callback = null
        vouchersListView = null
        super.onDestroyView()
    }
}