package net.noliaware.yumi_contributor.feature_profile.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserAccountDTO(
    @Json(name = "contributorAccount")
    val userProfileDTO: UserProfileDTO
)