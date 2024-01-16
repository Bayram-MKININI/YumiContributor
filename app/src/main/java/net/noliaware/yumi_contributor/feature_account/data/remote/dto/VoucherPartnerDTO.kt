package net.noliaware.yumi_contributor.feature_account.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherPartner

@JsonClass(generateAdapter = true)
data class VoucherPartnerDTO(
    @Json(name = "partnerInfoText")
    val partnerInfoText: String?,
    @Json(name = "partnerInfoURL")
    val partnerInfoURL: String?
) {
    fun toVoucherPartner() = VoucherPartner(
        partnerInfoText = partnerInfoText,
        partnerInfoURL = partnerInfoURL
    )
}