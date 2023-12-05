package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class CategoryUI(
    val categoryColor: Int,
    val categoryIcon: String?
) : Parcelable