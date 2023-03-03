package net.noliaware.yumi_contributor.feature_message.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InboxMessageDTO(
    @Json(name = "inboxMessage")
    val message: MessageDTO
)