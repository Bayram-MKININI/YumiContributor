package net.noliaware.yumi_contributor.feature_account.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.feature_account.data.remote.dto.VoucherDTO

@JsonClass(generateAdapter = true)
data class VouchersDTO(
    @Json(name = "userCategoryAvailableVouchers")
    val voucherDTOList: List<VoucherDTO>
)