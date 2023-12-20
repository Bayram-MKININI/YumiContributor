package net.noliaware.yumi_contributor.feature_message.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_OUTBOX_MESSAGE_LIST
import net.noliaware.yumi_contributor.commun.ApiParameters.LIMIT
import net.noliaware.yumi_contributor.commun.ApiParameters.LIST_PAGE_SIZE
import net.noliaware.yumi_contributor.commun.ApiParameters.OFFSET
import net.noliaware.yumi_contributor.commun.data.remote.RemoteApi
import net.noliaware.yumi_contributor.commun.domain.model.SessionData
import net.noliaware.yumi_contributor.commun.util.PaginationException
import net.noliaware.yumi_contributor.commun.util.ServiceError.ErrNone
import net.noliaware.yumi_contributor.commun.util.currentTimeInMillis
import net.noliaware.yumi_contributor.commun.util.generateToken
import net.noliaware.yumi_contributor.commun.util.getCommonWSParams
import net.noliaware.yumi_contributor.commun.util.handlePagingSourceError
import net.noliaware.yumi_contributor.commun.util.randomString
import net.noliaware.yumi_contributor.commun.util.resolvePaginatedListErrorIfAny
import net.noliaware.yumi_contributor.feature_message.domain.model.Message

class OutboxMessagePagingSource(
    private val api: RemoteApi,
    private val sessionData: SessionData
) : PagingSource<Int, Message>() {

    override fun getRefreshKey(
        state: PagingState<Int, Message>
    ) = state.anchorPosition?.let { anchorPosition ->
        state.closestPageToPosition(anchorPosition)?.let {
            it.prevKey?.plus(1) ?: it.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Message> {
        try {
            val position = params.key ?: 0

            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.fetchOutboxMessages(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = GET_OUTBOX_MESSAGE_LIST,
                    randomString = randomString
                ),
                params = generateGetMessagesListParams(
                    offset = position * LIST_PAGE_SIZE,
                    loadSize = params.loadSize,
                    tokenKey = GET_OUTBOX_MESSAGE_LIST
                )
            )

            val serviceError = resolvePaginatedListErrorIfAny(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = GET_OUTBOX_MESSAGE_LIST
            )

            if (serviceError !is ErrNone) {
                throw PaginationException(serviceError)
            }

            val moreItemsAvailable = remoteData.data?.messageDTOList?.lastOrNull()?.let { messageDTO ->
                if (messageDTO.messageRank != null && messageDTO.messageCount != null) {
                    messageDTO.messageRank < messageDTO.messageCount
                } else {
                    false
                }
            }

            val nextKey = if (moreItemsAvailable == true) {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / LIST_PAGE_SIZE)
            } else {
                null
            }
            val prevKey = if (position == 0) null else position - 1

            return LoadResult.Page(
                data = remoteData.data?.messageDTOList?.map { it.toMessage() }.orEmpty(),
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (ex: Exception) {
            return handlePagingSourceError(ex)
        }
    }

    private fun generateGetMessagesListParams(
        offset: Int,
        loadSize: Int,
        tokenKey: String
    ) = mutableMapOf(
        OFFSET to offset.toString(),
        LIMIT to loadSize.toString()
    ).also {
        it += getCommonWSParams(sessionData, tokenKey)
    }.toMap()
}