package net.noliaware.yumi_contributor.feature_account.presentation.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherPartner
import net.noliaware.yumi_contributor.feature_account.presentation.adapters.VoucherPartnersAdapter.*
import net.noliaware.yumi_contributor.feature_account.presentation.views.VoucherPartnerItemView
import net.noliaware.yumi_contributor.feature_account.presentation.views.VoucherPartnerItemView.*

class VoucherPartnersAdapter(
    private val onPartnerInfoAtIndex: (VoucherPartner) -> Unit
) : ListAdapter<VoucherPartner, VoucherPartnerItemViewHolder>(VoucherPartnerComparator) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = VoucherPartnerItemViewHolder(
        parent.inflate(R.layout.voucher_partner_item_layout)
    ).apply {
        partnerItemView.callback = VoucherPartnerItemViewCallback {
            if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                onPartnerInfoAtIndex.invoke(getItem(bindingAdapterPosition))
            }
        }
    }

    override fun onBindViewHolder(
        holder: VoucherPartnerItemViewHolder,
        position: Int
    ) {
        getItem(position)?.let { voucherRequest ->
            holder.partnerItemView.fillViewWithData(
                VoucherPartnerItemViewAdapter(
                    partnerLabel = voucherRequest.partnerInfoText,
                    linkAvailable = !voucherRequest.partnerInfoURL.isNullOrEmpty()
                )
            )
        }
    }

    object VoucherPartnerComparator : DiffUtil.ItemCallback<VoucherPartner>() {
        override fun areItemsTheSame(
            oldItem: VoucherPartner,
            newItem: VoucherPartner
        ): Boolean {
            return oldItem.partnerInfoText == newItem.partnerInfoText
        }

        override fun areContentsTheSame(
            oldItem: VoucherPartner,
            newItem: VoucherPartner
        ): Boolean {
            return oldItem == newItem
        }
    }

    inner class VoucherPartnerItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val partnerItemView = itemView as VoucherPartnerItemView
    }
}