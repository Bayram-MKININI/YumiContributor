package net.noliaware.yumi_contributor.feature_account.presentation.adapters

import androidx.recyclerview.widget.RecyclerView
import net.noliaware.yumi_contributor.feature_account.presentation.views.AccountsListItemView

class ManagedAccountViewHolder constructor(
    accountsListItemView: AccountsListItemView,
    private val onItemClicked: (Int) -> Unit,
    private val onPhoneButtonClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(accountsListItemView) {

    val heldItemView get() = itemView as AccountsListItemView

    private val accountsListItemViewCallback = AccountsListItemView.AccountsListItemViewCallback {
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
        heldItemView.callback = accountsListItemViewCallback
    }
}