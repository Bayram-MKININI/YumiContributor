package net.noliaware.yumi_contributor.commun.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ActionDTO(
    @Json(name = "type")
    val type: String = "",
    @Json(name = "params")
    val params: ParamsDTO
)