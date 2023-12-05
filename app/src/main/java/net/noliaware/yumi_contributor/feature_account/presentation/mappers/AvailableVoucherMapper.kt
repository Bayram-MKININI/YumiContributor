package net.noliaware.yumi_contributor.feature_account.presentation.mappers

import android.content.Context
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.DateTime.SHORT_DATE_FORMAT
import net.noliaware.yumi_contributor.commun.util.DecoratedText
import net.noliaware.yumi_contributor.commun.util.decorateWords
import net.noliaware.yumi_contributor.commun.util.getFontFromResources
import net.noliaware.yumi_contributor.commun.util.parseDateToFormat
import net.noliaware.yumi_contributor.feature_account.domain.model.Voucher
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherDeliveryStatus
import net.noliaware.yumi_contributor.feature_account.presentation.views.VoucherItemView.VoucherItemViewAdapter
import javax.inject.Inject

class AvailableVoucherMapper @Inject constructor() : VoucherMapper {

    override fun mapVoucher(
        context: Context,
        color: Int,
        voucher: Voucher
    ) = VoucherItemViewAdapter(
        isDeactivated = voucher.voucherDeliveryStatus != VoucherDeliveryStatus.AVAILABLE,
        color = color,
        title = voucher.productLabel.orEmpty(),
        hasOngoingRequests = voucher.voucherOngoingRequestCount > 0,
        highlight = mapHighlight(context, voucher),
        retailerDescription = context.getString(R.string.to_retrieve),
        retailerValue = voucher.retailerLabel
    )

    private fun mapHighlight(
        context: Context,
        voucher: Voucher
    ) = if (voucher.voucherExpiryDate != null) {
        val expiryDate = voucher.voucherExpiryDate.parseDateToFormat(SHORT_DATE_FORMAT)
        context.getString(
            R.string.usable_end_date,
            expiryDate
        ).decorateWords(
            wordsToDecorate = listOf(
                DecoratedText(
                    textToDecorate = expiryDate,
                    typeface = context.getFontFromResources(R.font.omnes_semibold_regular)
                )
            )
        )
    } else {
        val startDate = voucher.voucherStartDate?.parseDateToFormat(SHORT_DATE_FORMAT).orEmpty()
        context.getString(
            R.string.usable_start_date,
            startDate
        ).decorateWords(
            wordsToDecorate = listOf(
                DecoratedText(
                    textToDecorate = startDate,
                    typeface = context.getFontFromResources(R.font.omnes_semibold_regular)
                )
            )
        )
    }
}