package net.noliaware.yumi_contributor.commun.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ParamsDTO(
    @Json(name = "level")
    val level: String
)