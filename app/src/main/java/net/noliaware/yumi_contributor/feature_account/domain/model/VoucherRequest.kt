package net.noliaware.yumi_contributor.feature_account.domain.model

data class VoucherRequest(
    val voucherRequestId: String,
    val voucherRequestStatus: Int?,
    val voucherRequestDate: String?,
    val voucherRequestTime: String?,
    val voucherRequestLabel: String?,
    val voucherRequestComment: String?
)