package net.noliaware.yumi_contributor.feature_account.domain.model

import java.io.Serializable

data class Category(
    val categoryId: String,
    val categoryColor: String,
    val categoryIcon: String?,
    val categoryLabel: String,
    val categoryShortLabel: String,
    val categoryDescription: String,
    val availableVoucherCount: Int,
    val usedVoucherCount: Int
) : Serializable