package net.noliaware.yumi_contributor.feature_account.presentation.adapters

import androidx.recyclerview.widget.RecyclerView
import net.noliaware.yumi_contributor.feature_account.presentation.views.AccountItemView

class ManagedAccountViewHolder constructor(
    accountItemView: AccountItemView,
    private val onItemClicked: (Int) -> Unit,
    private val onPhoneButtonClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(accountItemView) {

    val heldItemView get() = itemView as AccountItemView

    private val accountItemViewCallback = AccountItemView.AccountsListItemViewCallback {
        if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
            onPhoneButtonClicked(bindingAdapterPosition)
        }
    }

    init {
        itemView.setOnClickListener {
            if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                onItemClicked(bindingAdapterPosition)
            }
        }
        heldItemView.callback = accountItemViewCallback
    }
}