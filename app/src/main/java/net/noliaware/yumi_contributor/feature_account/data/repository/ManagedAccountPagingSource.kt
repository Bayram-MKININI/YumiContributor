package net.noliaware.yumi_contributor.feature_account.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.noliaware.yumi_contributor.commun.GET_MANAGED_ACCOUNT_LIST
import net.noliaware.yumi_contributor.commun.LIMIT
import net.noliaware.yumi_contributor.commun.LIST_PAGE_SIZE
import net.noliaware.yumi_contributor.commun.OFFSET
import net.noliaware.yumi_contributor.commun.data.remote.RemoteApi
import net.noliaware.yumi_contributor.commun.domain.model.SessionData
import net.noliaware.yumi_contributor.commun.util.ErrorType
import net.noliaware.yumi_contributor.commun.util.PaginationException
import net.noliaware.yumi_contributor.commun.util.generateToken
import net.noliaware.yumi_contributor.commun.util.getCommonWSParams
import net.noliaware.yumi_contributor.commun.util.handlePaginatedListErrorIfAny
import net.noliaware.yumi_contributor.feature_account.domain.model.ManagedAccount
import java.util.UUID

class ManagedAccountPagingSource(
    private val api: RemoteApi, private val sessionData: SessionData
) : PagingSource<Int, ManagedAccount>() {

    override fun getRefreshKey(state: PagingState<Int, ManagedAccount>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ManagedAccount> {
        try {
            // Start refresh at page 1 if undefined.
            val nextPage = params.key ?: 0

            val timestamp = System.currentTimeMillis().toString()
            val randomString = UUID.randomUUID().toString()

            val remoteData = api.fetchManagedAccounts(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = GET_MANAGED_ACCOUNT_LIST,
                    randomString = randomString
                ),
                params = generateWSParams(nextPage, GET_MANAGED_ACCOUNT_LIST)
            )

            val errorType = handlePaginatedListErrorIfAny(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = GET_MANAGED_ACCOUNT_LIST
            )

            if (errorType != ErrorType.RECOVERABLE_ERROR) {
                throw PaginationException(errorType)
            }

            val profileRank = remoteData.data?.accountsDTOs?.last()?.accountRank ?: nextPage

            val moreItemsAvailable = remoteData.data?.accountsDTOs?.last()?.let { accountDTO ->
                if (accountDTO.accountRank != null && accountDTO.accountCount != null) {
                    accountDTO.accountRank < accountDTO.accountCount
                } else {
                    false
                }
            }

            val canLoadMore = moreItemsAvailable == true

            return LoadResult.Page(data = remoteData.data?.accountsDTOs?.map { it.toManagedAccount() }
                .orEmpty(),
                prevKey = null,// Only paging forward.
                nextKey = if (canLoadMore) profileRank else null)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    private fun generateWSParams(offset: Int, tokenKey: String) = mutableMapOf(
        LIMIT to LIST_PAGE_SIZE.toString(),
        OFFSET to offset.toString()
    ).also {
        it.plusAssign(getCommonWSParams(sessionData, tokenKey))
    }.toMap()
}