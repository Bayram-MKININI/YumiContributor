package net.noliaware.yumi_contributor.feature_profile.presentation.controllers

import android.content.Context
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.parseTimeString
import net.noliaware.yumi_contributor.commun.util.parseToShortDate
import net.noliaware.yumi_contributor.feature_account.domain.model.Voucher
import net.noliaware.yumi_contributor.feature_account.presentation.controllers.VoucherMapper
import net.noliaware.yumi_contributor.feature_account.presentation.views.VoucherItemView
import javax.inject.Inject

class UsedVoucherMapper @Inject constructor() : VoucherMapper {

    override fun mapVoucher(
        context: Context, voucher: Voucher
    ) = VoucherItemView.VoucherItemViewAdapter(
        title = voucher.productLabel.orEmpty(),
        expiryDate = context.getString(
            R.string.usage_date,
            parseToShortDate(voucher.voucherUseDate),
            parseTimeString(voucher.voucherUseTime)
        ),
        description = context.getString(
            R.string.retailer,
            voucher.retailerLabel
        )
    )
}
