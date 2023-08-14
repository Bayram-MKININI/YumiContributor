package net.noliaware.yumi_contributor.feature_alerts.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import net.noliaware.yumi_contributor.commun.GET_ALERT_LIST
import net.noliaware.yumi_contributor.commun.LIMIT
import net.noliaware.yumi_contributor.commun.LIST_PAGE_SIZE
import net.noliaware.yumi_contributor.commun.TIMESTAMP_OFFSET
import net.noliaware.yumi_contributor.commun.data.remote.RemoteApi
import net.noliaware.yumi_contributor.commun.domain.model.SessionData
import net.noliaware.yumi_contributor.commun.util.*
import net.noliaware.yumi_contributor.feature_alerts.domain.model.Alert
import java.util.*

class AlertPagingSource(
    private val api: RemoteApi,
    private val sessionData: SessionData
) : PagingSource<Long, Alert>() {

    override fun getRefreshKey(state: PagingState<Long, Alert>): Nothing? = null

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Alert> {
        try {
            // Start refresh at page 1 if undefined.
            val nextTimestamp = params.key ?: 0

            val timestamp = System.currentTimeMillis().toString()
            val randomString = UUID.randomUUID().toString()

            val remoteData = api.fetchAlertList(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = GET_ALERT_LIST,
                    randomString = randomString
                ),
                params = generateGetAlertsListParams(nextTimestamp, GET_ALERT_LIST)
            )

            val errorType = handlePaginatedListErrorIfAny(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = GET_ALERT_LIST
            )

            if (errorType != ErrorType.RECOVERABLE_ERROR) {
                throw PaginationException(errorType)
            }

            val alertTimestamp =
                remoteData.data?.alertDTOList?.lastOrNull()?.alertTimestamp ?: nextTimestamp

            val moreItemsAvailable = remoteData.data?.alertDTOList?.lastOrNull()?.let { alertDTO ->
                alertDTO.alertRank < alertDTO.alertCount
            }

            val canLoadMore = moreItemsAvailable == true

            return LoadResult.Page(
                data = remoteData.data?.alertDTOList?.map { it.toAlert() }.orEmpty(),
                prevKey = null,// Only paging forward.
                nextKey = if (canLoadMore) alertTimestamp else null
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    private fun generateGetAlertsListParams(timestamp: Long, tokenKey: String) = mutableMapOf(
        TIMESTAMP_OFFSET to timestamp.toString(),
        LIMIT to LIST_PAGE_SIZE.toString()
    ).also {
        it.plusAssign(getCommonWSParams(sessionData, tokenKey))
    }.toMap()
}