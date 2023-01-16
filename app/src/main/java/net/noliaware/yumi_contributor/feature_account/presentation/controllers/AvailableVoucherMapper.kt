package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.content.Context
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.parseToShortDate
import net.noliaware.yumi_contributor.feature_account.domain.model.Voucher
import net.noliaware.yumi_contributor.feature_account.presentation.views.VoucherItemView
import javax.inject.Inject

class AvailableVoucherMapper @Inject constructor() : VoucherMapper {

    override fun mapVoucher(
        context: Context, voucher: Voucher
    ) = VoucherItemView.VoucherItemViewAdapter(
        title = voucher.productLabel.orEmpty(),
        expiryDate = context.getString(
            R.string.expiry_date,
            parseToShortDate(voucher.voucherExpiryDate)
        ),
        description = context.getString(
            R.string.retailer,
            voucher.retailerLabel
        )
    )
}
