package net.noliaware.yumi_contributor.commun.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.commun.domain.model.AppMessage
import net.noliaware.yumi_contributor.commun.domain.model.AppMessageLevel
import net.noliaware.yumi_contributor.commun.domain.model.AppMessageType

@JsonClass(generateAdapter = true)
class AppMessageDTO(
    @Json(name = "messageType")
    val messageType: String,
    @Json(name = "messageLevel")
    val messageLevel: String,
    @Json(name = "messageTitle")
    val messageTitle: String,
    @Json(name = "messageBody")
    val messageBody: String
) {
    fun toAppMessage() = AppMessage(
        type = AppMessageType.fromString(messageType),
        level = AppMessageLevel.fromInt(messageLevel.toInt()),
        title = messageTitle,
        body = messageBody
    )
}