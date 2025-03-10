package net.noliaware.yumi_contributor.feature_account.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_MANAGED_ACCOUNT_LIST
import net.noliaware.yumi_contributor.commun.ApiParameters.LIMIT
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
import net.noliaware.yumi_contributor.feature_account.domain.model.ManagedAccount

class ManagedAccountPagingSource(
    private val api: RemoteApi, private val sessionData: SessionData
) : PagingSource<Int, ManagedAccount>() {

    override fun getRefreshKey(
        state: PagingState<Int, ManagedAccount>
    ) = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ManagedAccount> {
        try {
            val offset = params.key ?: 0

            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.fetchManagedAccounts(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = GET_MANAGED_ACCOUNT_LIST,
                    randomString = randomString
                ),
                params = generateWSParams(
                    offset = offset,
                    loadSize = params.loadSize,
                    tokenKey = GET_MANAGED_ACCOUNT_LIST
                )
            )

            val serviceError = resolvePaginatedListErrorIfAny(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = GET_MANAGED_ACCOUNT_LIST
            )

            if (serviceError !is ErrNone) {
                throw PaginationException(serviceError)
            }

            val lastAccountRank = remoteData.data?.accountsDTOs?.lastOrNull()?.accountRank ?: offset

            val moreItemsAvailable = remoteData.data?.accountsDTOs?.lastOrNull()?.let { accountDTO ->
                if (accountDTO.accountRank != null && accountDTO.accountCount != null) {
                    accountDTO.accountRank < accountDTO.accountCount
                } else {
                    false
                }
            }

            return LoadResult.Page(
                data = remoteData.data?.accountsDTOs?.map { it.toManagedAccount() }.orEmpty(),
                prevKey = null,// Only paging forward.
                nextKey = if (moreItemsAvailable == true) lastAccountRank else null
            )
        } catch (ex: Exception) {
            return handlePagingSourceError(ex)
        }
    }

    private fun generateWSParams(
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