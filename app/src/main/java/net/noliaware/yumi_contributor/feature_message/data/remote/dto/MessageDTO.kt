package net.noliaware.yumi_contributor.feature_message.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import net.noliaware.yumi_contributor.commun.domain.model.Priority
import net.noliaware.yumi_contributor.feature_message.domain.model.Message

@JsonClass(generateAdapter = true)
data class MessageDTO(
    @Json(name = "messageId")
    val messageId: String,
    @Json(name = "messageDate")
    val messageDate: String,
    @Json(name = "messageTime")
    val messageTime: String,
    @Json(name = "messageType")
    val messageType: String?,
    @Json(name = "messagePriority")
    val messagePriority: Int?,
    @Json(name = "messageSubject")
    val messageSubject: String,
    @Json(name = "messagePreview")
    val messagePreview: String?,
    @Json(name = "messageReadStatus")
    val messageReadStatus: Int?,
    @Json(name = "messageBody")
    val messageBody: String?,
    @Json(name = "messageRank")
    val messageRank: Int?,
    @Json(name = "messageCount")
    val messageCount: Int?
) {
    fun toMessage() = Message(
        messageId = messageId,
        messageDate = messageDate,
        messageTime = messageTime,
        messageType = messageType,
        messagePriority = Priority.fromValue(messagePriority),
        messageSubject = messageSubject,
        messagePreview = messagePreview,
        messageReadStatus = messageReadStatus,
        messageBody = messageBody
    )
}