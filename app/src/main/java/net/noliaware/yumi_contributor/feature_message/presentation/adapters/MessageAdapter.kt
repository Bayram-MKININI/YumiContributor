package net.noliaware.yumi_contributor.feature_message.presentation.adapters

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.DATE_TIME_SOURCE_FORMAT
import net.noliaware.yumi_contributor.commun.DAY_OF_MONTH_NUMERICAL_DATE_FORMAT
import net.noliaware.yumi_contributor.commun.HOURS_TIME_FORMAT
import net.noliaware.yumi_contributor.commun.NUMERICAL_DATE_FORMAT
import net.noliaware.yumi_contributor.commun.SINGLE_DAY_DATE_FORMAT
import net.noliaware.yumi_contributor.commun.presentation.adapters.ItemViewHolder
import net.noliaware.yumi_contributor.commun.presentation.mappers.PriorityMapper
import net.noliaware.yumi_contributor.commun.util.inflate
import net.noliaware.yumi_contributor.commun.util.parseDateToFormat
import net.noliaware.yumi_contributor.commun.util.parseTimeToFormat
import net.noliaware.yumi_contributor.feature_message.domain.model.Message
import net.noliaware.yumi_contributor.feature_message.presentation.views.MessageItemView
import net.noliaware.yumi_contributor.feature_message.presentation.views.MessageItemView.MessageItemViewAdapter
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

class MessageAdapter(
    private val onItemClicked: (Message) -> Unit
) : PagingDataAdapter<Message, ItemViewHolder<MessageItemView>>(MessageComparator) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = ItemViewHolder<MessageItemView>(
        parent.inflate(R.layout.message_item_layout)
    ) { position ->
        getItem(position)?.let { onItemClicked(it) }
    }

    override fun onBindViewHolder(holder: ItemViewHolder<MessageItemView>, position: Int) {
        getItem(position)?.let { message ->
            holder.heldItemView.fillViewWithData(
                mapAdapter(message)
            )
        }
    }

    private fun mapAdapter(
        message: Message
    ) = MessageItemViewAdapter(
        priorityIconRes = PriorityMapper().mapPriorityIcon(message.messagePriority),
        subject = if (message.messageType.isNullOrEmpty()) {
            message.messageSubject
        } else {
            "${message.messageType} ${message.messageSubject}"
        },
        time = resolveTime(message.messageDate, message.messageTime),
        body = message.messagePreview.orEmpty(),
        opened = message.isMessageRead
    )

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

    object MessageComparator : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(
            oldItem: Message,
            newItem: Message
        ): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(
            oldItem: Message,
            newItem: Message
        ): Boolean {
            return oldItem == newItem
        }
    }
}