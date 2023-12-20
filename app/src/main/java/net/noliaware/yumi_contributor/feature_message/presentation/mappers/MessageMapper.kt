package net.noliaware.yumi_contributor.feature_message.presentation.mappers

import android.content.Context
import net.noliaware.yumi_contributor.commun.DateTime.DATE_TIME_SOURCE_FORMAT
import net.noliaware.yumi_contributor.commun.DateTime.DAY_OF_MONTH_NUMERICAL_DATE_FORMAT
import net.noliaware.yumi_contributor.commun.DateTime.HOURS_TIME_FORMAT
import net.noliaware.yumi_contributor.commun.DateTime.NUMERICAL_DATE_FORMAT
import net.noliaware.yumi_contributor.commun.DateTime.SINGLE_DAY_DATE_FORMAT
import net.noliaware.yumi_contributor.commun.presentation.mappers.PriorityMapper
import net.noliaware.yumi_contributor.commun.util.parseDateToFormat
import net.noliaware.yumi_contributor.commun.util.parseTimeToFormat
import net.noliaware.yumi_contributor.feature_message.domain.model.Message
import net.noliaware.yumi_contributor.feature_message.presentation.views.MessageItemView.MessageItemViewAdapter
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

interface MessageMapper {
    fun mapMessage(
        context: Context,
        message: Message
    ) = MessageItemViewAdapter(
        priorityIconRes = PriorityMapper().mapPriorityIcon(message.messagePriority),
        subject = if (message.messageType.isNullOrEmpty()) {
            message.messageSubject
        } else {
            "${message.messageType} ${message.messageSubject}"
        },
        time = resolveTime(message.messageDate, message.messageTime),
        mail = resolveMail(context, message),
        body = message.messagePreview.orEmpty(),
        opened = message.messageHasBeenRead
    )

    fun resolveMail(context: Context, message: Message): String

    private fun resolveTime(messageDateStr: String, messageTimeStr: String): String {

        val messageDate = LocalDate.parse(
            "$messageDateStr $messageTimeStr".padEnd(DATE_TIME_SOURCE_FORMAT.length, '0'),
            DateTimeFormatter.ofPattern(DATE_TIME_SOURCE_FORMAT)
        )
        val weeksPassed = ChronoUnit.WEEKS.between(messageDate, LocalDate.now())
        val inSameYear = messageDate.year.absoluteValue == Year.now().value

        return if (weeksPassed < 1) {
            "${messageDateStr.parseDateToFormat(SINGLE_DAY_DATE_FORMAT)} ${
                messageTimeStr.parseTimeToFormat(
                    HOURS_TIME_FORMAT
                )
            }"
        } else if (inSameYear) {
            "${messageDateStr.parseDateToFormat(SINGLE_DAY_DATE_FORMAT)} ${
                messageDateStr.parseDateToFormat(
                    DAY_OF_MONTH_NUMERICAL_DATE_FORMAT
                )
            }"
        } else {
            messageDateStr.parseDateToFormat(NUMERICAL_DATE_FORMAT)
        }
    }
}