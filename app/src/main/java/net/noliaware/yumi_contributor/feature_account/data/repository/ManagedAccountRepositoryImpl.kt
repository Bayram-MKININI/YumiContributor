package net.noliaware.yumi_contributor.feature_account.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.noliaware.yumi_contributor.commun.GET_DATA_PER_CATEGORY
import net.noliaware.yumi_contributor.commun.GET_USED_VOUCHER_LIST_BY_CATEGORY
import net.noliaware.yumi_contributor.commun.GET_VOUCHER
import net.noliaware.yumi_contributor.commun.GET_VOUCHER_STATUS
import net.noliaware.yumi_contributor.commun.LIST_PAGE_SIZE
import net.noliaware.yumi_contributor.commun.SELECTED_USER
import net.noliaware.yumi_contributor.commun.SELECT_USER
import net.noliaware.yumi_contributor.commun.USE_VOUCHER
import net.noliaware.yumi_contributor.commun.VOUCHER_ID
import net.noliaware.yumi_contributor.commun.data.remote.RemoteApi
import net.noliaware.yumi_contributor.commun.domain.model.SessionData
import net.noliaware.yumi_contributor.commun.util.ErrorType
import net.noliaware.yumi_contributor.commun.util.Resource
import net.noliaware.yumi_contributor.commun.util.generateToken
import net.noliaware.yumi_contributor.commun.util.getCommonWSParams
import net.noliaware.yumi_contributor.commun.util.handleSessionWithNoFailure
import net.noliaware.yumi_contributor.feature_account.domain.model.Category
import net.noliaware.yumi_contributor.feature_account.domain.model.Voucher
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStatus
import okio.IOException
import retrofit2.HttpException
import java.util.UUID

class ManagedAccountRepositoryImpl(
    private val api: RemoteApi,
    private val sessionData: SessionData
) : ManagedAccountRepository {

    override fun getManagedAccountList() = Pager(
        PagingConfig(
            pageSize = LIST_PAGE_SIZE,
            enablePlaceholders = false
        )
    ) {
        ManagedAccountPagingSource(api, sessionData)
    }.flow

    override fun selectManagedAccountForId(accountId: String): Flow<Resource<String>> = flow {

        emit(Resource.Loading())

        try {

            val timestamp = System.currentTimeMillis().toString()
            val randomString = UUID.randomUUID().toString()

            val remoteData = api.selectUserForId(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(timestamp, SELECT_USER, randomString),
                params = generateSelectAccountParams(accountId, SELECT_USER)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = SELECT_USER,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {
                remoteData.data?.let { accountDataDTO ->
                    emit(
                        Resource.Success(
                            data = accountDataDTO.userId,
                            appMessage = remoteData.message?.toAppMessage()
                        )
                    )
                }
            }

        } catch (ex: HttpException) {
            emit(Resource.Error(errorType = ErrorType.SYSTEM_ERROR))
        } catch (ex: IOException) {
            emit(Resource.Error(errorType = ErrorType.NETWORK_ERROR))
        }
    }

    private fun generateSelectAccountParams(accountId: String, tokenKey: String) = mutableMapOf(
        SELECTED_USER to accountId
    ).also { it.plusAssign(getCommonWSParams(sessionData, tokenKey)) }

    override fun getCategories(): Flow<Resource<List<Category>>> = flow {

        emit(Resource.Loading())

        try {

            val timestamp = System.currentTimeMillis().toString()
            val randomString = UUID.randomUUID().toString()

            val remoteData = api.fetchDataByCategory(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = GET_DATA_PER_CATEGORY,
                    randomString = randomString
                ),
                params = getCommonWSParams(sessionData, GET_DATA_PER_CATEGORY)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = GET_DATA_PER_CATEGORY,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {
                remoteData.data?.let { voucherCategoriesDTO ->
                    voucherCategoriesDTO.categoryDTOs?.let { categoriesDTO ->
                        emit(
                            Resource.Success(
                                data = categoriesDTO.map { it.toCategory() },
                                appMessage = remoteData.message?.toAppMessage()
                            )
                        )
                    }
                }
            }

        } catch (ex: HttpException) {
            emit(Resource.Error(errorType = ErrorType.SYSTEM_ERROR))
        } catch (ex: IOException) {
            emit(Resource.Error(errorType = ErrorType.NETWORK_ERROR))
        }
    }

    override fun getVoucherList(categoryId: String) = Pager(
        PagingConfig(
            pageSize = LIST_PAGE_SIZE,
            enablePlaceholders = false
        )
    ) {
        VoucherPagingSource(api, sessionData, categoryId)
    }.flow

    override fun getUsedVoucherList(categoryId: String) = Pager(
        PagingConfig(
            pageSize = LIST_PAGE_SIZE,
            enablePlaceholders = false
        )
    ) {
        UsedVoucherPagingSource(api, sessionData, categoryId)
    }.flow

    override fun getVoucherById(voucherId: String): Flow<Resource<Voucher>> = flow {

        emit(Resource.Loading())

        try {

            val timestamp = System.currentTimeMillis().toString()
            val randomString = UUID.randomUUID().toString()

            val remoteData = api.fetchVoucherForId(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = GET_VOUCHER,
                    randomString = randomString
                ),
                params = generateVoucherByIdParams(voucherId, GET_VOUCHER)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = GET_VOUCHER,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {
                remoteData.data?.let { getVoucherDTO ->
                    emit(
                        Resource.Success(
                            data = getVoucherDTO.voucherDTO.toVoucher(sessionData.sessionId),
                            appMessage = remoteData.message?.toAppMessage()
                        )
                    )
                }
            }

        } catch (ex: HttpException) {
            emit(Resource.Error(errorType = ErrorType.SYSTEM_ERROR))
        } catch (ex: IOException) {
            emit(Resource.Error(errorType = ErrorType.NETWORK_ERROR))
        }
    }

    override fun getVoucherStatusById(voucherId: String): Flow<Resource<VoucherStatus>> = flow {

        emit(Resource.Loading())

        try {

            val timestamp = System.currentTimeMillis().toString()
            val randomString = UUID.randomUUID().toString()

            val remoteData = api.fetchVoucherStatusForId(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = GET_VOUCHER_STATUS,
                    randomString = randomString
                ),
                params = generateVoucherByIdParams(voucherId, GET_VOUCHER_STATUS)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = GET_VOUCHER_STATUS,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {
                remoteData.data?.let { getVoucherStatusDTO ->
                    emit(
                        Resource.Success(
                            data = getVoucherStatusDTO.toVoucherStatus(),
                            appMessage = remoteData.message?.toAppMessage()
                        )
                    )
                }
            }

        } catch (ex: HttpException) {
            emit(Resource.Error(errorType = ErrorType.SYSTEM_ERROR))
        } catch (ex: IOException) {
            emit(Resource.Error(errorType = ErrorType.NETWORK_ERROR))
        }
    }

    override fun useVoucherById(voucherId: String): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        try {

            val timestamp = System.currentTimeMillis().toString()
            val randomString = UUID.randomUUID().toString()

            val remoteData = api.useVoucher(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp,
                    USE_VOUCHER,
                    randomString
                ),
                params = generateVoucherByIdParams(voucherId, USE_VOUCHER)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = USE_VOUCHER,
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

        } catch (ex: HttpException) {
            emit(Resource.Error(errorType = ErrorType.SYSTEM_ERROR))
        } catch (ex: IOException) {
            emit(Resource.Error(errorType = ErrorType.NETWORK_ERROR))
        }
    }

    private fun generateVoucherByIdParams(voucherId: String, tokenKey: String) = mutableMapOf(
        VOUCHER_ID to voucherId
    ).also { it.plusAssign(getCommonWSParams(sessionData, tokenKey)) }
}