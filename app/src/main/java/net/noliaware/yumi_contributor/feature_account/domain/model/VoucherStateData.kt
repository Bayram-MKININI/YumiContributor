package net.noliaware.yumi_contributor.feature_account.domain.model

data class VoucherStateData(
    val voucherStatus: VoucherStatus?,
    val voucherUseDate: String?,
    val voucherUseTime: String?
)