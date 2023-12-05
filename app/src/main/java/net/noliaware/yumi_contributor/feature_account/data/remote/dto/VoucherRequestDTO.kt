package net.noliaware.yumi_contributor.feature_account.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherRequest

@JsonClass(generateAdapter = true)
data class VoucherRequestDTO(
    @Json(name = "voucherRequestId")
    val voucherRequestId: String,
    @Json(name = "voucherRequestStatus")
    val voucherRequestStatus: Int?,
    @Json(name = "voucherRequestDate")
    val voucherRequestDate: String?,
    @Json(name = "voucherRequestTime")
    val voucherRequestTime: String?,
    @Json(name = "voucherRequestLabel")
    val voucherRequestLabel: String?,
    @Json(name = "voucherRequestComment")
    val voucherRequestComment: String?
) {
    fun toVoucherRequest() = VoucherRequest(
        voucherRequestId = voucherRequestId,
        voucherRequestStatus = voucherRequestStatus,
        voucherRequestDate = voucherRequestDate,
        voucherRequestTime = voucherRequestTime,
        voucherRequestLabel = voucherRequestLabel,
        voucherRequestComment = voucherRequestComment
    )
}