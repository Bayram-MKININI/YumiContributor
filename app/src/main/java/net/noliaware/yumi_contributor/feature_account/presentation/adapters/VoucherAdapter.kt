package net.noliaware.yumi_contributor.feature_account.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.adapters.ItemViewHolder
import net.noliaware.yumi_contributor.feature_account.domain.model.Voucher
import net.noliaware.yumi_contributor.feature_account.presentation.controllers.VoucherMapper
import net.noliaware.yumi_contributor.feature_account.presentation.views.VoucherItemView

class VoucherAdapter(
    private val voucherMapper: VoucherMapper,
    private val onItemClicked: (Voucher) -> Unit
) : PagingDataAdapter<Voucher, ItemViewHolder<VoucherItemView>>(VoucherComparator) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ItemViewHolder<VoucherItemView>(
        LayoutInflater.from(parent.context).inflate(R.layout.voucher_item_layout, parent, false)
    ) { position ->
        getItem(position)?.let { onItemClicked(it) }
    }

    override fun onBindViewHolder(holder: ItemViewHolder<VoucherItemView>, position: Int) {
        getItem(position)?.let { voucher ->
            holder.heldItemView.fillViewWithData(
                voucherMapper.mapVoucher(holder.heldItemView.context, voucher)
            )
        }
    }

    object VoucherComparator : DiffUtil.ItemCallback<Voucher>() {
        override fun areItemsTheSame(
            oldItem: Voucher,
            newItem: Voucher
        ): Boolean {
            return oldItem.voucherId == newItem.voucherId
        }

        override fun areContentsTheSame(
            oldItem: Voucher,
            newItem: Voucher
        ): Boolean {
            return oldItem == newItem
        }
    }
}