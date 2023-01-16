package net.noliaware.yumi_contributor.feature_account.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStatus

@JsonClass(generateAdapter = true)
data class GetVoucherStatusDTO(
    @Json(name = "userVoucherStatus")
    val voucherStatus: Int,
) {
    fun toVoucherStatus() = when (voucherStatus) {
        0 -> VoucherStatus.CANCELED
        1 -> VoucherStatus.USABLE
        2 -> VoucherStatus.CONSUMED
        else -> VoucherStatus.INEXISTENT
    }
}