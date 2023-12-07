package net.noliaware.yumi_contributor.feature_message.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import net.noliaware.yumi_contributor.commun.util.Resource
import net.noliaware.yumi_contributor.feature_message.domain.model.Message

interface MessageRepository {

    fun getReceivedMessageList(): Flow<PagingData<Message>>

    fun getSentMessageList(): Flow<PagingData<Message>>

    fun getInboxMessageForId(messageId: String): Flow<Resource<Message>>

    fun getOutboxMessageForId(messageId: String): Flow<Resource<Message>>

    fun sendMessage(
        recipients: List<String>?,
        subject: String?,
        messagePriority: Int?,
        messageId: String?,
        messageBody: String
    ): Flow<Resource<Boolean>>

    fun deleteInboxMessageForId(messageId: String): Flow<Resource<Boolean>>

    fun deleteOutboxMessageForId(messageId: String): Flow<Resource<Boolean>>
}