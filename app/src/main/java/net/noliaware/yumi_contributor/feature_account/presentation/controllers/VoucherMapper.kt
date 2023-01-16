package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.content.Context
import net.noliaware.yumi_contributor.feature_account.domain.model.Voucher
import net.noliaware.yumi_contributor.feature_account.presentation.views.VoucherItemView

interface VoucherMapper {
    fun mapVoucher(context: Context, voucher: Voucher): VoucherItemView.VoucherItemViewAdapter
}