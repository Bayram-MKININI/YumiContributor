package net.noliaware.yumi_contributor.feature_message.presentation.mappers

import android.content.Context
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.feature_message.domain.model.Message
import javax.inject.Inject

class SentMessageMapper @Inject constructor() : MessageMapper {
    override fun resolveMail(
        context: Context,
        message: Message
    ) = "${context.getString(R.string.mail_to)} ${message.messageRecipient}"
}