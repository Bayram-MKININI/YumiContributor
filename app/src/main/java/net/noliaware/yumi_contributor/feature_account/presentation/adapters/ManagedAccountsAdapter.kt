package net.noliaware.yumi_contributor.feature_account.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.adapters.ItemViewHolder
import net.noliaware.yumi_contributor.feature_account.domain.model.ManagedAccount
import net.noliaware.yumi_contributor.feature_login.presentation.views.AccountCategoryView
import net.noliaware.yumi_contributor.feature_login.presentation.views.AccountItemView

class ManagedAccountsAdapter(
    private val onItemClicked: (ManagedAccount) -> Unit
) : PagingDataAdapter<ManagedAccount, ItemViewHolder<AccountItemView>>(ManagedAccountComparator) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ItemViewHolder<AccountItemView>(
        LayoutInflater.from(parent.context).inflate(R.layout.account_item_layout, parent, false)
    ) { position ->
        getItem(position)?.let { onItemClicked(it) }
    }

    override fun onBindViewHolder(holder: ItemViewHolder<AccountItemView>, position: Int) {
        getItem(position)?.let { accountData ->
            holder.heldItemView.fillViewWithData(
                mapAdapter(accountData, holder)
            )
        }
    }

    private fun mapAdapter(
        managedAccount: ManagedAccount,
        holder: ItemViewHolder<AccountItemView>
    ) = AccountItemView.AccountItemViewAdapter(
        title = "${managedAccount.title} ${managedAccount.firstName} ${managedAccount.lastName}",
        phoneNumber = holder.heldItemView.context.getString(
            R.string.mobile_short,
            managedAccount.cellPhoneNumber
        ),
        lastLogin = managedAccount.login.orEmpty()
    ).also { accountItemViewAdapter ->
        managedAccount.categories.filter {
            it.availableVoucherCount > 0
        }.map { category ->
            AccountCategoryView.AccountCategoryViewAdapter(
                iconName = category.categoryIcon.orEmpty(),
                title = category.categoryShortLabel,
                count = category.availableVoucherCount
            )
        }.also {
            accountItemViewAdapter.accountCategoryViewAdapters.addAll(it)
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