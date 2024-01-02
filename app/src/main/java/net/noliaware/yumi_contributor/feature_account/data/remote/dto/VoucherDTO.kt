package net.noliaware.yumi_contributor.feature_account.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.feature_account.domain.model.Voucher
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherDeliveryStatus
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherRetrievalMode
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStatus

@JsonClass(generateAdapter = true)
data class VoucherDTO(
    @Json(name = "voucherId")
    val voucherId: String,
    @Json(name = "voucherNumber")
    val voucherNumber: String,
    @Json(name = "voucherCode")
    val voucherCode: String?,
    @Json(name = "voucherStatus")
    val voucherStatus: Int?,
    @Json(name = "voucherStartDate")
    val voucherStartDate: String?,
    @Json(name = "voucherExpiryDate")
    val voucherExpiryDate: String?,
    @Json(name = "voucherUseDate")
    val voucherUseDate: String?,
    @Json(name = "voucherUseTime")
    val voucherUseTime: String?,
    @Json(name = "voucherUseMode")
    val voucherUseMode: Int?,
    @Json(name = "voucherDeliveryStatus")
    val voucherDeliveryStatus: Int?,
    @Json(name = "productLabel")
    val productLabel: String?,
    @Json(name = "voucherRequestCount")
    val voucherRequestCount: Int?,
    @Json(name = "productDescription")
    val productDescription: String?,
    @Json(name = "productWebpage")
    val productWebpage: String?,
    @Json(name = "retailerType")
    val retailerType: String?,
    @Json(name = "retailerLabel")
    val retailerLabel: String?,
    @Json(name = "retailerAddress")
    val retailerAddress: String?,
    @Json(name = "retailerAddressComplement")
    val retailerAddressComplement: String?,
    @Json(name = "retailerPostcode")
    val retailerPostcode: String?,
    @Json(name = "retailerCity")
    val retailerCity: String?,
    @Json(name = "retailerCountry")
    val retailerCountry: String?,
    @Json(name = "retailerAddressLatitude")
    val retailerAddressLatitude: String?,
    @Json(name = "retailerAddressLongitude")
    val retailerAddressLongitude: String?,
    @Json(name = "retailerPhoneNumber")
    val retailerPhoneNumber: String?,
    @Json(name = "retailerCellPhoneNumber")
    val retailerCellPhoneNumber: String?,
    @Json(name = "retailerEmail")
    val retailerEmail: String?,
    @Json(name = "retailerWebsite")
    val retailerWebsite: String?,
    @Json(name = "partnerInfoText")
    val partnerInfoText: String?,
    @Json(name = "partnerInfoURL")
    val partnerInfoURL: String?,
    @Json(name = "voucherPartnerDisplayStatus")
    val voucherPartnerDisplayStatus: Int?,
    @Json(name = "voucherRank")
    val voucherRank: Int?,
    @Json(name = "voucherCount")
    val voucherCount: Int?
) {
    fun toVoucher(sessionId: String? = null) = Voucher(
        voucherId = voucherId,
        voucherNumber = voucherNumber,
        voucherCode = voucherCode + if (!sessionId.isNullOrBlank()) sessionId else "",
        voucherStatus = VoucherStatus.fromValue(voucherStatus),
        voucherStartDate = voucherStartDate,
        voucherExpiryDate = voucherExpiryDate,
        voucherUseDate = voucherUseDate,
        voucherUseTime = voucherUseTime,
        voucherRetrievalMode = VoucherRetrievalMode.fromValue(voucherUseMode),
        voucherDeliveryStatus = VoucherDeliveryStatus.fromValue(voucherDeliveryStatus),
        productLabel = productLabel,
        voucherOngoingRequestCount = voucherRequestCount ?: 0,
        productDescription = productDescription,
        productWebpage = productWebpage,
        retailerType = retailerType,
        retailerLabel = retailerLabel,
        retailerAddress = retailerAddress,
        retailerAddressComplement = retailerAddressComplement,
        retailerPostcode = retailerPostcode,
        retailerCity = retailerCity,
        retailerCountry = retailerCountry,
        retailerAddressLatitude = retailerAddressLatitude,
        retailerAddressLongitude = retailerAddressLongitude,
        retailerPhoneNumber = retailerPhoneNumber,
        retailerCellPhoneNumber = retailerCellPhoneNumber,
        retailerEmail = retailerEmail,
        retailerWebsite = retailerWebsite,
        partnerInfoText = partnerInfoText,
        partnerInfoURL = partnerInfoURL,
        isPartnerInfoAvailable = voucherPartnerDisplayStatus == 1
    )
}