package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.FragmentKeys.REFRESH_VOUCHER_CATEGORY_LIST_REQUEST_KEY
import net.noliaware.yumi_contributor.commun.FragmentKeys.REFRESH_VOUCHER_LIST_REQUEST_KEY
import net.noliaware.yumi_contributor.commun.presentation.adapters.ListLoadStateAdapter
import net.noliaware.yumi_contributor.commun.util.DecoratedText
import net.noliaware.yumi_contributor.commun.util.decorateWords
import net.noliaware.yumi_contributor.commun.util.getColorCompat
import net.noliaware.yumi_contributor.commun.util.handlePaginationError
import net.noliaware.yumi_contributor.commun.util.navDismiss
import net.noliaware.yumi_contributor.commun.util.safeNavigate
import net.noliaware.yumi_contributor.feature_account.presentation.adapters.VoucherAdapter
import net.noliaware.yumi_contributor.feature_account.presentation.mappers.AvailableVoucherMapper
import net.noliaware.yumi_contributor.feature_account.presentation.views.VouchersListView
import net.noliaware.yumi_contributor.feature_account.presentation.views.VouchersListView.VouchersListViewAdapter
import net.noliaware.yumi_contributor.feature_account.presentation.views.VouchersListView.VouchersListViewCallback

@AndroidEntryPoint
class AvailableVouchersListFragment : AppCompatDialogFragment() {

    private var vouchersListView: VouchersListView? = null
    private val args by navArgs<AvailableVouchersListFragmentArgs>()
    private val viewModel by viewModels<AvailableVouchersListFragmentViewModel>()

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
        vouchersListView?.callback = readMailViewCallback
        vouchersListView?.voucherAdapter = VoucherAdapter(
            color = args.selectedCategory.categoryColor,
            voucherMapper = AvailableVoucherMapper()
        ) { voucher ->
            findNavController().safeNavigate(
                AvailableVouchersListFragmentDirections.actionAvailableVouchersListFragmentToVoucherDetailsFragment(
                    categoryUI = CategoryUI(
                        categoryColor = args.selectedCategory.categoryColor,
                        categoryIcon = args.selectedCategory.categoryIcon
                    ),
                    voucherId = voucher.voucherId,
                    requestTypes = args.requestTypes
                )
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFragmentListener()
        setUpViewWithSelectedCategory()
        vouchersListView?.setLoadingVisible(true)
        collectFlows()
    }

    private fun setUpFragmentListener() {
        setFragmentResultListener(
            REFRESH_VOUCHER_LIST_REQUEST_KEY
        ) { _, _ ->
            vouchersListView?.voucherAdapter?.refresh()
            viewModel.dataShouldRefresh = true
        }
    }

    private fun setUpViewWithSelectedCategory() {
        val title = getString(
            R.string.available_vouchers_list,
            args.selectedCategory.categoryLabel
        )
        vouchersListView?.fillViewWithData(
            VouchersListViewAdapter(
                color = args.selectedCategory.categoryColor,
                iconName = args.selectedCategory.categoryIcon,
                title = title.decorateWords(
                    wordsToDecorate = listOf(
                        DecoratedText(
                            textToDecorate = getString(R.string.available).lowercase(),
                            color = context?.getColorCompat(R.color.colorPrimary) ?: Color.TRANSPARENT
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

    private val readMailViewCallback: VouchersListViewCallback by lazy {
        VouchersListViewCallback {
            navDismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (viewModel.dataShouldRefresh) {
            setFragmentResult(
                REFRESH_VOUCHER_CATEGORY_LIST_REQUEST_KEY,
                bundleOf()
            )
        }
    }

    override fun onDestroyView() {
        vouchersListView?.callback = null
        vouchersListView = null
        super.onDestroyView()
    }
}