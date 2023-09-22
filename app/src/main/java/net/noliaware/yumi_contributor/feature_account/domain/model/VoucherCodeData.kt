package net.noliaware.yumi_contributor.feature_account.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VoucherCodeData(
    val voucherId: String?,
    val productLabel: String?,
    val voucherDate: String?,
    val voucherExpiryDate: String?,
    val voucherCode: String?,
    val voucherCodeSize: Int
) : Parcelable