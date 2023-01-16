package net.noliaware.yumi_contributor.feature_account.domain.model

data class Voucher(
    val voucherId: String,
    val voucherCode: String?,
    val voucherDate: String?,
    val voucherExpiryDate: String?,
    val voucherUseDate: String?,
    val voucherUseTime: String?,
    val productLabel: String?,
    val productDescription: String?,
    val productWebpage: String?,
    val retailerType: String?,
    val retailerLabel: String?,
    val retailerAddress: String?,
    val retailerAddressComplement: String?,
    val retailerPostcode: String?,
    val retailerCity: String?,
    val retailerCountry: String?,
    val retailerAddressLatitude: String?,
    val retailerAddressLongitude: String?,
    val retailerPhoneNumber: String?,
    val retailerCellPhoneNumber: String?,
    val retailerEmail: String?,
    val retailerWebsite: String?,
    val partnerInfoText: String?,
    val partnerInfoURL: String?
)