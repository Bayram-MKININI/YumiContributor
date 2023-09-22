package net.noliaware.yumi_contributor.feature_account.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Category(
    val categoryId: String,
    val categoryColor: Int,
    val categoryIcon: String?,
    val categoryLabel: String,
    val categoryShortLabel: String,
    val categoryDescription: String,
    val availableVoucherCount: Int,
    val usedVoucherCount: Int,
    val cancelledVoucherCount: Int
) : Parcelable