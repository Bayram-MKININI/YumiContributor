package net.noliaware.yumi_contributor.feature_account.presentation.adapters

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.feature_account.domain.model.ManagedAccount
import net.noliaware.yumi_contributor.feature_account.presentation.mappers.ManagedAccountMapper
import net.noliaware.yumi_contributor.feature_account.presentation.views.AccountsListItemView

class PaginatedManagedAccountsAdapter(
    private val accountMapper: ManagedAccountMapper,
    private val onItemClicked: (ManagedAccount) -> Unit,
    private val onPhoneButtonClicked: (ManagedAccount) -> Unit
) : PagingDataAdapter<ManagedAccount, ManagedAccountViewHolder>(ManagedAccountComparator) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ManagedAccountViewHolder(
        accountsListItemView = parent.inflate(
            R.layout.account_item_layout
        ) as AccountsListItemView,
        onItemClicked = { position ->
            getItem(position)?.let {
                onItemClicked(it)
            }
        },
        onPhoneButtonClicked = { position ->
            getItem(position)?.let {
                onPhoneButtonClicked(it)
            }
        }
    )

    override fun onBindViewHolder(holder: ManagedAccountViewHolder, position: Int) {
        getItem(position)?.let { accountData ->
            holder.heldItemView.fillViewWithData(
                accountMapper.mapAccount(accountData)
            )
        }
    }

    object ManagedAccountComparator : DiffUtil.ItemCallback<ManagedAccount>() {
        override fun areItemsTheSame(
            oldItem: ManagedAccount,
            newItem: ManagedAccount
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ManagedAccount,
            newItem: ManagedAccount
        ): Boolean {
            return oldItem == newItem
        }
    }
}