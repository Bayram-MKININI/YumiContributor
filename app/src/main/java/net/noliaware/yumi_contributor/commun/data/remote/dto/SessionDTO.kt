package net.noliaware.yumi_contributor.commun.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SessionDTO(
    @Json(name = "sessionId")
    val sessionId: String = "",
    @Json(name = "sessionToken")
    val sessionToken: String = ""
)