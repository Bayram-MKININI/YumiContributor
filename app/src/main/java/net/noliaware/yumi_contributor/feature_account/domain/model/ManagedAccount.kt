package net.noliaware.yumi_contributor.feature_account.domain.model

import java.io.Serializable

data class ManagedAccount(
    val login: String,
    val title: String,
    val firstName: String,
    val lastName: String,
    val cellPhoneNumber: String,
    val categories: List<Category> = listOf()
) : Serializable