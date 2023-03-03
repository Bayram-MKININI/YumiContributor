package net.noliaware.yumi_contributor.feature_account.presentation.mappers

import net.noliaware.yumi_contributor.commun.util.formatNumber
import net.noliaware.yumi_contributor.feature_account.domain.model.ManagedAccount
import net.noliaware.yumi_contributor.feature_account.presentation.views.AccountsListItemView.AccountItemViewAdapter
import javax.inject.Inject

class ManagedAccountMapper @Inject constructor() {

    fun mapAccount(
        managedAccount: ManagedAccount
    ) = AccountItemViewAdapter(
        title = "${managedAccount.title} ${managedAccount.firstName} ${managedAccount.lastName}",
        count = managedAccount.availableVoucherCount.formatNumber()
    )
}
