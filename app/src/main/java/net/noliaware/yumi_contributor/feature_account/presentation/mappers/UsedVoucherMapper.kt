package net.noliaware.yumi_contributor.feature_account.presentation.mappers

import android.content.Context
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.parseToShortDate
import net.noliaware.yumi_contributor.feature_account.domain.model.Voucher
import net.noliaware.yumi_contributor.feature_account.presentation.views.VoucherItemView
import javax.inject.Inject

class UsedVoucherMapper @Inject constructor() : VoucherMapper {

    override fun mapVoucher(
        context: Context,
        color: Int,
        voucher: Voucher
    ) = VoucherItemView.VoucherItemViewAdapter(
        color = color,
        title = voucher.productLabel.orEmpty(),
        highlightDescription = context.getString(R.string.validation_date),
        highlightValue = parseToShortDate(voucher.voucherUseDate),
        retailer = context.getString(
            R.string.retailer_label,
            voucher.retailerLabel
        )
    )
}
