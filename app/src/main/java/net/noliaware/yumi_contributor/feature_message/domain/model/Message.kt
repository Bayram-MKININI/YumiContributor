package net.noliaware.yumi_contributor.feature_message.domain.model

data class Message(
    val messageId: String,
    val messageDate: String,
    val messageTime: String,
    val messageSubject: String,
    val messagePreview: String?,
    val messageType: String?,
    val messageReadStatus: Int?,
    val messageBody: String?
)