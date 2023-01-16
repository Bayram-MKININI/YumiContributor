package net.noliaware.yumi_contributor.feature_message.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.presentation.adapters.ItemViewHolder
import net.noliaware.yumi_contributor.commun.util.parseToShortDate
import net.noliaware.yumi_contributor.feature_message.domain.model.Message
import net.noliaware.yumi_contributor.feature_message.presentation.views.MessageItemView

class MessageAdapter(
    private val onItemClicked: (Message) -> Unit
) : PagingDataAdapter<Message, ItemViewHolder<MessageItemView>>(MessageComparator) {

    override fun onBindViewHolder(holder: ItemViewHolder<MessageItemView>, position: Int) {
        getItem(position)?.let { message ->
            holder.heldItemView.fillViewWithData(
                mapAdapter(message)
            )
        }
    }

    private fun mapAdapter(
        message: Message
    ) = MessageItemView.MessageItemViewAdapter(
        subject = message.messageSubject,
        time = parseToShortDate(message.messageDate),
        body = message.messagePreview.orEmpty()
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemViewHolder<MessageItemView>(
            LayoutInflater.from(parent.context).inflate(R.layout.message_item_layout, parent, false)
        ) { position ->
            getItem(position)?.let { onItemClicked(it) }
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