package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import java.io.Serializable

data class CategoryUI(
    val categoryColor: Int?,
    val categoryIcon: String?,
    val categoryLabel: String?
) : Serializable