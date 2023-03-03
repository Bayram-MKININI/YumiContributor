package net.noliaware.yumi_contributor.feature_account.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.commun.util.parseHexColor
import net.noliaware.yumi_contributor.feature_account.domain.model.Category

@JsonClass(generateAdapter = true)
data class CategoryDTO(
    @Json(name = "categoryId")
    val categoryId: String,
    @Json(name = "categoryLabel")
    val categoryLabel: String,
    @Json(name = "categoryShortLabel")
    val categoryShortLabel: String,
    @Json(name = "categoryDescription")
    val categoryDescription: String,
    @Json(name = "categoryColor")
    val categoryColor: String,
    @Json(name = "categoryIcon")
    val categoryIcon: String?,
    @Json(name = "availableVoucherCount")
    val availableVoucherCount: Int?,
    @Json(name = "usedVoucherCount")
    val usedVoucherCount: Int?,
    @Json(name = "canceledVoucherCount")
    val cancelledVoucherCount: Int?
) {
    fun toCategory() = Category(
        categoryId = categoryId,
        categoryColor = categoryColor.parseHexColor(),
        categoryIcon = categoryIcon,
        categoryLabel = categoryLabel,
        categoryShortLabel = categoryShortLabel,
        categoryDescription = categoryDescription,
        availableVoucherCount = availableVoucherCount ?: 0,
        usedVoucherCount = usedVoucherCount ?: 0,
        cancelledVoucherCount = cancelledVoucherCount ?: 0
    )
}