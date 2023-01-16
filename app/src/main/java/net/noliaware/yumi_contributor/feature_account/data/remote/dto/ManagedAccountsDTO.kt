package net.noliaware.yumi_contributor.feature_account.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ManagedAccountsDTO(
    @Json(name = "contributorUsers")
    val accountsDTOs: List<ManagedAccountDTO> = listOf()
)