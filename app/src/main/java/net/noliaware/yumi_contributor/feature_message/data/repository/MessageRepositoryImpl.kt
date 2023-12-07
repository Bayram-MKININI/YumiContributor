package net.noliaware.yumi_contributor.feature_message.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.noliaware.yumi_contributor.commun.ApiConstants.DELETE_INBOX_MESSAGE
import net.noliaware.yumi_contributor.commun.ApiConstants.DELETE_OUTBOX_MESSAGE
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_INBOX_MESSAGE
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_OUTBOX_MESSAGE
import net.noliaware.yumi_contributor.commun.ApiConstants.SEND_MESSAGE
import net.noliaware.yumi_contributor.commun.ApiParameters.LIST_PAGE_SIZE
import net.noliaware.yumi_contributor.commun.ApiParameters.MESSAGE_BODY
import net.noliaware.yumi_contributor.commun.ApiParameters.MESSAGE_ID
import net.noliaware.yumi_contributor.commun.ApiParameters.MESSAGE_PRIORITY
import net.noliaware.yumi_contributor.commun.ApiParameters.MESSAGE_SUBJECT
import net.noliaware.yumi_contributor.commun.ApiParameters.MESSAGE_TO
import net.noliaware.yumi_contributor.commun.data.remote.RemoteApi
import net.noliaware.yumi_contributor.commun.domain.model.SessionData
import net.noliaware.yumi_contributor.commun.util.Resource
import net.noliaware.yumi_contributor.commun.util.currentTimeInMillis
import net.noliaware.yumi_contributor.commun.util.generateToken
import net.noliaware.yumi_contributor.commun.util.getCommonWSParams
import net.noliaware.yumi_contributor.commun.util.handleRemoteCallError
import net.noliaware.yumi_contributor.commun.util.handleSessionWithNoFailure
import net.noliaware.yumi_contributor.commun.util.randomString
import net.noliaware.yumi_contributor.feature_message.domain.model.Message
import net.noliaware.yumi_contributor.feature_message.domain.repository.MessageRepository
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val api: RemoteApi,
    private val sessionData: SessionData
) : MessageRepository {

    override fun getReceivedMessageList() = Pager(
        PagingConfig(
            pageSize = LIST_PAGE_SIZE,
            enablePlaceholders = false
        )
    ) {
        InboxMessagePagingSource(api, sessionData)
    }.flow

    override fun getSentMessageList() = Pager(
        PagingConfig(
            pageSize = LIST_PAGE_SIZE,
            enablePlaceholders = false
        )
    ) {
        OutboxMessagePagingSource(api, sessionData)
    }.flow

    override fun getInboxMessageForId(messageId: String): Flow<Resource<Message>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.fetchInboxMessageForId(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = GET_INBOX_MESSAGE,
                    randomString = randomString
                ),
                params = generateGetMessageParams(messageId, GET_INBOX_MESSAGE)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = GET_INBOX_MESSAGE,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {
                remoteData.data?.let { inboxMessageDTO ->
                    emit(
                        Resource.Success(
                            data = inboxMessageDTO.message.toMessage(),
                            appMessage = remoteData.message?.toAppMessage()
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            handleRemoteCallError(ex)
        }
    }

    private fun generateGetMessageParams(
        messageId: String,
        tokenKey: String
    ) = mutableMapOf(
        MESSAGE_ID to messageId
    ).also {
        it.plusAssign(getCommonWSParams(sessionData, tokenKey))
    }

    override fun getOutboxMessageForId(messageId: String): Flow<Resource<Message>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.fetchOutboxMessageForId(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = GET_OUTBOX_MESSAGE,
                    randomString = randomString
                ),
                params = generateGetMessageParams(messageId, GET_OUTBOX_MESSAGE)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = GET_OUTBOX_MESSAGE,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {
                remoteData.data?.let { outboxMessageDTO ->
                    emit(
                        Resource.Success(
                            data = outboxMessageDTO.message.toMessage(),
                            appMessage = remoteData.message?.toAppMessage()
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            handleRemoteCallError(ex)
        }
    }

    override fun sendMessage(
        recipients: List<String>?,
        subject: String?,
        messagePriority: Int?,
        messageId: String?,
        messageBody: String
    ): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.sendMessage(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = SEND_MESSAGE,
                    randomString = randomString
                ),
                params = generateSendMessageParams(
                    recipients = recipients,
                    subject = subject,
                    messagePriority = messagePriority,
                    messageId = messageId,
                    messageBody = messageBody,
                    tokenKey = SEND_MESSAGE
                )
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = SEND_MESSAGE,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {
                emit(
                    Resource.Success(
                        data = remoteData.data != null,
                        appMessage = remoteData.message?.toAppMessage()
                    )
                )
            }

        } catch (ex: Exception) {
            handleRemoteCallError(ex)
        }
    }

    private fun generateSendMessageParams(
        recipients: List<String>? = null,
        subject: String? = null,
        messagePriority: Int?,
        messageId: String? = null,
        messageBody: String,
        tokenKey: String
    ) = mutableMapOf(
        MESSAGE_BODY to messageBody
    ).also { map ->
        recipients?.let { map[MESSAGE_TO] = recipients.joinToString(";") }
        subject?.let { map[MESSAGE_SUBJECT] = subject }
        messagePriority?.let { map[MESSAGE_PRIORITY] = messagePriority.toString() }
        messageId?.let { map[MESSAGE_ID] = messageId }
        map.plusAssign(getCommonWSParams(sessionData, tokenKey))
    }.toMap()

    override fun deleteInboxMessageForId(messageId: String): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.deleteInboxMessageForId(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = DELETE_INBOX_MESSAGE,
                    randomString = randomString
                ),
                params = generateGetMessageParams(messageId, DELETE_INBOX_MESSAGE)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = DELETE_INBOX_MESSAGE,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {
                emit(
                    Resource.Success(
                        data = remoteData.data != null,
                        appMessage = remoteData.message?.toAppMessage()
                    )
                )
            }

        } catch (ex: Exception) {
            handleRemoteCallError(ex)
        }
    }

    override fun deleteOutboxMessageForId(messageId: String): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.deleteOutboxMessageForId(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = DELETE_OUTBOX_MESSAGE,
                    randomString = randomString
                ),
                params = generateGetMessageParams(messageId, DELETE_OUTBOX_MESSAGE)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = DELETE_OUTBOX_MESSAGE,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {
                emit(
                    Resource.Success(
                        data = remoteData.data != null,
                        appMessage = remoteData.message?.toAppMessage()
                    )
                )
            }

        } catch (ex: Exception) {
            handleRemoteCallError(ex)
        }
    }
}