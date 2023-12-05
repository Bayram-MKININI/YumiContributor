package net.noliaware.yumi_contributor.feature_login.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.feature_login.domain.model.VoucherRequestType

@JsonClass(generateAdapter = true)
data class VoucherRequestTypeDTO(
    @Json(name = "voucherRequestTypeId")
    val requestTypeId: Int,
    @Json(name = "voucherRequestTypeLabel")
    val requestTypeLabel: String
) {
    fun toVoucherRequestType() = VoucherRequestType(
        requestTypeId = requestTypeId,
        requestTypeLabel = requestTypeLabel
    )
}