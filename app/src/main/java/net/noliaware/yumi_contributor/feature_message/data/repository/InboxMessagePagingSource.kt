package net.noliaware.yumi_contributor.feature_message.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.noliaware.yumi_contributor.commun.GET_INBOX_MESSAGE_LIST
import net.noliaware.yumi_contributor.commun.LIMIT
import net.noliaware.yumi_contributor.commun.LIST_PAGE_SIZE
import net.noliaware.yumi_contributor.commun.OFFSET
import net.noliaware.yumi_contributor.commun.data.remote.RemoteApi
import net.noliaware.yumi_contributor.commun.domain.model.SessionData
import net.noliaware.yumi_contributor.commun.util.*
import net.noliaware.yumi_contributor.feature_message.domain.model.Message
import java.util.*

class InboxMessagePagingSource(
    private val api: RemoteApi,
    private val sessionData: SessionData
) : PagingSource<Int, Message>() {

    override fun getRefreshKey(state: PagingState<Int, Message>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        try {
            val nextPage = params.key ?: 0

            val timestamp = System.currentTimeMillis().toString()
            val randomString = UUID.randomUUID().toString()

            val remoteData = api.fetchInboxMessages(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = GET_INBOX_MESSAGE_LIST,
                    randomString = randomString
                ),
                params = generateGetMessagesListParams(nextPage, GET_INBOX_MESSAGE_LIST)
            )

            val errorType = handlePaginatedListErrorIfAny(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = GET_INBOX_MESSAGE_LIST
            )

            if (errorType != ErrorType.RECOVERABLE_ERROR) {
                throw PaginationException(errorType)
            }

            val messageRank = remoteData.data?.messageDTOList?.lastOrNull()?.messageRank ?: nextPage

            val moreItemsAvailable = remoteData.data?.messageDTOList?.lastOrNull()?.let { messageDTO ->
                if (messageDTO.messageRank != null && messageDTO.messageCount != null) {
                    messageDTO.messageRank < messageDTO.messageCount
                } else {
                    false
                }
            }

            val canLoadMore = moreItemsAvailable == true

            return LoadResult.Page(
                data = remoteData.data?.messageDTOList?.map { it.toMessage() }.orEmpty(),
                prevKey = null,// Only paging forward.
                nextKey = if (canLoadMore) messageRank else null
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    private fun generateGetMessagesListParams(offset: Int, tokenKey: String) = mutableMapOf(
        LIMIT to LIST_PAGE_SIZE.toString(),
        OFFSET to offset.toString()
    ).also {
        it.plusAssign(getCommonWSParams(sessionData, tokenKey))
    }.toMap()
}