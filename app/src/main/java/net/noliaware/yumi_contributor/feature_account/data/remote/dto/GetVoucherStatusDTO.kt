package net.noliaware.yumi_contributor.feature_account.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStateData
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStatus

@JsonClass(generateAdapter = true)
data class GetVoucherStatusDTO(
    @Json(name = "voucherStatus")
    val voucherStatus: Int,
    @Json(name = "voucherUseDate")
    val voucherUseDate: String?,
    @Json(name = "voucherUseTime")
    val voucherUseTime: String?
) {
    fun toVoucherStateData() = VoucherStateData(
        voucherStatus = VoucherStatus.fromValue(voucherStatus),
        voucherUseDate = voucherUseDate,
        voucherUseTime = voucherUseTime
    )
}