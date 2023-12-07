package net.noliaware.yumi_contributor.feature_message.domain.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import net.noliaware.yumi_contributor.commun.domain.model.Priority

@Keep
@Parcelize
data class Message(
    val messageId: String,
    val messageDate: String,
    val messageTime: String,
    val messageSender: String?,
    val messageRecipient: String?,
    val messageType: String?,
    val messagePriority: Priority?,
    val messageSubject: String,
    val messagePreview: String?,
    val isMessageRead: Boolean,
    val messageBody: String?
) : Parcelable