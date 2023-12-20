package net.noliaware.yumi_contributor.feature_account.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_USED_VOUCHER_LIST_BY_CATEGORY
import net.noliaware.yumi_contributor.commun.ApiParameters.CATEGORY_ID
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
import net.noliaware.yumi_contributor.feature_account.domain.model.Voucher

class UsedVoucherPagingSource(
    private val api: RemoteApi,
    private val sessionData: SessionData,
    private val categoryId: String
) : PagingSource<Int, Voucher>() {

    override fun getRefreshKey(
        state: PagingState<Int, Voucher>
    ): Nothing? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Voucher> {
        try {
            val position = params.key ?: 0

            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.fetchUsedVouchersForCategory(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = GET_USED_VOUCHER_LIST_BY_CATEGORY,
                    randomString = randomString
                ),
                params = generateWSParams(
                    categoryId = categoryId,
                    offset = position,
                    loadSize = params.loadSize,
                    tokenKey = GET_USED_VOUCHER_LIST_BY_CATEGORY
                )
            )

            val serviceError = resolvePaginatedListErrorIfAny(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = GET_USED_VOUCHER_LIST_BY_CATEGORY
            )

            if (serviceError !is ErrNone) {
                throw PaginationException(serviceError)
            }

            val moreItemsAvailable = remoteData.data?.voucherDTOList?.lastOrNull()?.let { voucherDTO ->
                if (voucherDTO.voucherRank != null && voucherDTO.voucherCount != null) {
                    voucherDTO.voucherRank < voucherDTO.voucherCount
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

            return LoadResult.Page(
                data = remoteData.data?.voucherDTOList?.map { it.toVoucher() }.orEmpty(),
                prevKey = null,// Only paging forward.
                nextKey = nextKey
            )
        } catch (ex: Exception) {
            return handlePagingSourceError(ex)
        }
    }

    private fun generateWSParams(
        categoryId: String,
        offset: Int,
        loadSize: Int,
        tokenKey: String
    ) = mutableMapOf(
        CATEGORY_ID to categoryId,
        OFFSET to offset.toString(),
        LIMIT to loadSize.toString()
    ).also {
        it += getCommonWSParams(sessionData, tokenKey)
    }.toMap()
}