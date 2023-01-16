package net.noliaware.yumi_contributor.feature_account.domain.model

import java.io.Serializable

data class VoucherCodeData(
    val voucherId: String?,
    val productLabel: String?,
    val voucherDate: String?,
    val voucherExpiryDate: String?,
    val voucherCode: String?,
    val voucherCodeSize: Int
) : Serializable