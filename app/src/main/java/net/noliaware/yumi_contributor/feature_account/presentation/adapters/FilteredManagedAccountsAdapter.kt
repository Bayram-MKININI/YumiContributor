package net.noliaware.yumi_contributor.feature_account.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.feature_account.domain.model.ManagedAccount
import net.noliaware.yumi_contributor.feature_account.presentation.mappers.ManagedAccountMapper
import net.noliaware.yumi_contributor.feature_account.presentation.views.AccountsListItemView

class FilteredManagedAccountsAdapter(
    private val dataSet: List<ManagedAccount>,
    private val accountMapper: ManagedAccountMapper,
    private val onItemClicked: (ManagedAccount) -> Unit,
    private val onPhoneButtonClicked: (ManagedAccount) -> Unit
) : RecyclerView.Adapter<ManagedAccountViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ManagedAccountViewHolder(
        accountsListItemView = LayoutInflater.from(parent.context).inflate(
            R.layout.account_item_layout,
            parent,
            false
        ) as AccountsListItemView,
        onItemClicked = { position ->
            onItemClicked(dataSet[position])
        },
        onPhoneButtonClicked = { position ->
            onPhoneButtonClicked(dataSet[position])
        }
    )

    override fun onBindViewHolder(
        holder: ManagedAccountViewHolder,
        position: Int
    ) {
        holder.heldItemView.fillViewWithData(
            accountMapper.mapAccount(dataSet[position])
        )
    }

    override fun getItemCount() = dataSet.size
}