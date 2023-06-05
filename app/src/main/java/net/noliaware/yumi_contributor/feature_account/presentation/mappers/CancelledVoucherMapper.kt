package net.noliaware.yumi_contributor.feature_account.presentation.mappers

import android.content.Context
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.SHORT_DATE_FORMAT
import net.noliaware.yumi_contributor.commun.util.parseDateToFormat
import net.noliaware.yumi_contributor.feature_account.domain.model.Voucher
import net.noliaware.yumi_contributor.feature_account.presentation.views.VoucherItemView
import javax.inject.Inject

class CancelledVoucherMapper @Inject constructor() : VoucherMapper {

    override fun mapVoucher(
        context: Context,
        color: Int,
        voucher: Voucher
    ) = VoucherItemView.VoucherItemViewAdapter(
        color = color,
        title = voucher.productLabel.orEmpty(),
        highlightDescription = context.getString(R.string.cancellation_date),
        highlightValue = voucher.voucherUseDate?.parseDateToFormat(SHORT_DATE_FORMAT).orEmpty(),
        retailerDescription = context.getString(R.string.retailer),
        retailerValue = voucher.retailerLabel
    )
}
