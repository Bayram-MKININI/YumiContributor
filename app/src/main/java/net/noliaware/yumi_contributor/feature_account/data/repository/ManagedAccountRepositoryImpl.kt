package net.noliaware.yumi_contributor.feature_account.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.noliaware.yumi_contributor.commun.ApiConstants.DELETE_VOUCHER_REQUEST
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_AVAILABLE_DATA_PER_CATEGORY
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_CANCELLED_DATA_PER_CATEGORY
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_FILTER_USERS_LIST
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_SINGLE_MANAGED_ACCOUNT
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_USED_DATA_PER_CATEGORY
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_VOUCHER
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_VOUCHER_REQUEST_LIST
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_VOUCHER_STATUS
import net.noliaware.yumi_contributor.commun.ApiConstants.SELECT_USER
import net.noliaware.yumi_contributor.commun.ApiConstants.SEND_VOUCHER_REQUEST
import net.noliaware.yumi_contributor.commun.ApiConstants.SET_PRIVACY_POLICY_READ_STATUS
import net.noliaware.yumi_contributor.commun.ApiConstants.USE_VOUCHER
import net.noliaware.yumi_contributor.commun.ApiParameters.LIST_PAGE_SIZE
import net.noliaware.yumi_contributor.commun.ApiParameters.SELECTED_USER
import net.noliaware.yumi_contributor.commun.ApiParameters.USER_ID
import net.noliaware.yumi_contributor.commun.ApiParameters.VOUCHER_ID
import net.noliaware.yumi_contributor.commun.ApiParameters.VOUCHER_REQUEST_COMMENT
import net.noliaware.yumi_contributor.commun.ApiParameters.VOUCHER_REQUEST_ID
import net.noliaware.yumi_contributor.commun.ApiParameters.VOUCHER_REQUEST_TYPE_ID
import net.noliaware.yumi_contributor.commun.data.remote.RemoteApi
import net.noliaware.yumi_contributor.commun.domain.model.SessionData
import net.noliaware.yumi_contributor.commun.util.Resource
import net.noliaware.yumi_contributor.commun.util.currentTimeInMillis
import net.noliaware.yumi_contributor.commun.util.generateToken
import net.noliaware.yumi_contributor.commun.util.getCommonWSParams
import net.noliaware.yumi_contributor.commun.util.handleRemoteCallError
import net.noliaware.yumi_contributor.commun.util.handleSessionWithNoFailure
import net.noliaware.yumi_contributor.commun.util.randomString
import net.noliaware.yumi_contributor.feature_account.domain.model.Category
import net.noliaware.yumi_contributor.feature_account.domain.model.ManagedAccount
import net.noliaware.yumi_contributor.feature_account.domain.model.Voucher
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherRequest
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStateData
import net.noliaware.yumi_contributor.feature_account.domain.repository.ManagedAccountRepository
import javax.inject.Inject

class ManagedAccountRepositoryImpl @Inject constructor(
    private val api: RemoteApi,
    private val sessionData: SessionData
) : ManagedAccountRepository {

    override fun updatePrivacyPolicyReadStatus(): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.updatePrivacyPolicyReadStatus(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = SET_PRIVACY_POLICY_READ_STATUS,
                    randomString = randomString
                ),
                params = getCommonWSParams(sessionData, SET_PRIVACY_POLICY_READ_STATUS)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = SET_PRIVACY_POLICY_READ_STATUS,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {
                emit(
                    Resource.Success(
                        data = remoteData.data?.result == 1,
                        appMessage = remoteData.message?.toAppMessage()
                    )
                )
            }

        } catch (ex: Exception) {
            handleRemoteCallError(ex)
        }
    }

    override fun getFilterUsers(): Flow<Resource<List<ManagedAccount>>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.fetchFilterUsersList(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = GET_FILTER_USERS_LIST,
                    randomString = randomString
                ),
                params = getCommonWSParams(sessionData, GET_FILTER_USERS_LIST)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = GET_FILTER_USERS_LIST,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {
                remoteData.data?.let { usersDTO ->
                    emit(
                        Resource.Success(
                            data = usersDTO.usersDTOs.map { it.toManagedAccount() },
                            appMessage = remoteData.message?.toAppMessage()
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            handleRemoteCallError(ex)
        }
    }

    override fun getManagedAccountForId(
        userId: String
    ): Flow<Resource<ManagedAccount>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.fetchManagedAccount(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(timestamp, GET_SINGLE_MANAGED_ACCOUNT, randomString),
                params = getManagedAccountParams(userId, GET_SINGLE_MANAGED_ACCOUNT)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = GET_SINGLE_MANAGED_ACCOUNT,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {
                remoteData.data?.let { accountDataDTO ->
                    emit(
                        Resource.Success(
                            data = accountDataDTO.managedAccountDTO.toManagedAccount(),
                            appMessage = remoteData.message?.toAppMessage()
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            handleRemoteCallError(ex)
        }
    }

    private fun getManagedAccountParams(
        userId: String,
        tokenKey: String
    ) = mutableMapOf(
        USER_ID to userId
    ).also { it.plusAssign(getCommonWSParams(sessionData, tokenKey)) }

    override fun getManagedAccountList() = Pager(
        PagingConfig(
            pageSize = LIST_PAGE_SIZE,
            enablePlaceholders = false
        )
    ) {
        ManagedAccountPagingSource(api, sessionData)
    }.flow

    override fun selectManagedAccountForId(
        accountId: String
    ): Flow<Resource<String>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

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

        } catch (ex: Exception) {
            handleRemoteCallError(ex)
        }
    }

    private fun generateSelectAccountParams(
        accountId: String,
        tokenKey: String
    ) = mutableMapOf(
        SELECTED_USER to accountId
    ).also { it.plusAssign(getCommonWSParams(sessionData, tokenKey)) }

    override fun getAvailableCategories(): Flow<Resource<List<Category>>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.fetchAvailableVoucherDataByCategory(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = GET_AVAILABLE_DATA_PER_CATEGORY,
                    randomString = randomString
                ),
                params = getCommonWSParams(sessionData, GET_AVAILABLE_DATA_PER_CATEGORY)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = GET_AVAILABLE_DATA_PER_CATEGORY,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {
                remoteData.data?.let { voucherAvailableCategoriesDTO ->
                    voucherAvailableCategoriesDTO.categoryDTOs?.let { categoriesDTO ->
                        emit(
                            Resource.Success(
                                data = categoriesDTO.map { it.toCategory() },
                                appMessage = remoteData.message?.toAppMessage()
                            )
                        )
                    }
                }
            }

        } catch (ex: Exception) {
            handleRemoteCallError(ex)
        }
    }

    override fun getAvailableVoucherList(
        categoryId: String
    ) = Pager(
        PagingConfig(
            pageSize = LIST_PAGE_SIZE,
            enablePlaceholders = false
        )
    ) {
        AvailableVoucherPagingSource(api, sessionData, categoryId)
    }.flow

    override fun getUsedCategories(): Flow<Resource<List<Category>>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.fetchUsedVoucherDataByCategory(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = GET_USED_DATA_PER_CATEGORY,
                    randomString = randomString
                ),
                params = getCommonWSParams(sessionData, GET_USED_DATA_PER_CATEGORY)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = GET_USED_DATA_PER_CATEGORY,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {
                remoteData.data?.let { voucherUsedCategoriesDTO ->
                    voucherUsedCategoriesDTO.categoryDTOs?.let { categoriesDTO ->
                        emit(
                            Resource.Success(
                                data = categoriesDTO.map { it.toCategory() },
                                appMessage = remoteData.message?.toAppMessage()
                            )
                        )
                    }
                }
            }

        } catch (ex: Exception) {
            handleRemoteCallError(ex)
        }
    }

    override fun getUsedVoucherList(
        categoryId: String
    ) = Pager(
        PagingConfig(
            pageSize = LIST_PAGE_SIZE,
            enablePlaceholders = false
        )
    ) {
        UsedVoucherPagingSource(api, sessionData, categoryId)
    }.flow

    override fun getCancelledCategories(): Flow<Resource<List<Category>>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.fetchCancelledVoucherDataByCategory(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = GET_CANCELLED_DATA_PER_CATEGORY,
                    randomString = randomString
                ),
                params = getCommonWSParams(sessionData, GET_CANCELLED_DATA_PER_CATEGORY)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = GET_CANCELLED_DATA_PER_CATEGORY,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {
                remoteData.data?.let { voucherUsedCategoriesDTO ->
                    voucherUsedCategoriesDTO.categoryDTOs?.let { categoriesDTO ->
                        emit(
                            Resource.Success(
                                data = categoriesDTO.map { it.toCategory() },
                                appMessage = remoteData.message?.toAppMessage()
                            )
                        )
                    }
                }
            }

        } catch (ex: Exception) {
            handleRemoteCallError(ex)
        }
    }

    override fun getCancelledVoucherList(
        categoryId: String
    ) = Pager(
        PagingConfig(
            pageSize = LIST_PAGE_SIZE,
            enablePlaceholders = false
        )
    ) {
        CancelledVoucherPagingSource(api, sessionData, categoryId)
    }.flow

    override fun getVoucherById(
        voucherId: String
    ): Flow<Resource<Voucher>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.fetchVoucherForId(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = GET_VOUCHER,
                    randomString = randomString
                ),
                params = generateVoucherIdParams(voucherId, GET_VOUCHER)
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

        } catch (ex: Exception) {
            handleRemoteCallError(ex)
        }
    }

    override fun sendVoucherRequestWithId(
        voucherId: String,
        voucherRequestTypeId: Int,
        voucherRequestComment: String
    ): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.sendVoucherRequestWithId(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = SEND_VOUCHER_REQUEST,
                    randomString = randomString
                ),
                params = generateVoucherRequestParams(
                    voucherId,
                    voucherRequestTypeId,
                    voucherRequestComment,
                    SEND_VOUCHER_REQUEST
                )
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = SEND_VOUCHER_REQUEST,
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

    private fun generateVoucherRequestParams(
        voucherId: String,
        voucherRequestTypeId: Int,
        voucherRequestComment: String,
        tokenKey: String
    ) = mutableMapOf(
        VOUCHER_ID to voucherId,
        VOUCHER_REQUEST_TYPE_ID to voucherRequestTypeId.toString(),
        VOUCHER_REQUEST_COMMENT to voucherRequestComment
    ).also { it += getCommonWSParams(sessionData, tokenKey) }

    override fun getVoucherRequestListById(
        voucherId: String
    ): Flow<Resource<List<VoucherRequest>>> = flow {

            emit(Resource.Loading())

            try {
                val timestamp = currentTimeInMillis()
                val randomString = randomString()

                val remoteData = api.fetchVoucherRequestListForId(
                    timestamp = timestamp,
                    saltString = randomString,
                    token = generateToken(
                        timestamp = timestamp,
                        methodName = GET_VOUCHER_REQUEST_LIST,
                        randomString = randomString
                    ),
                    params = generateVoucherIdParams(voucherId, GET_VOUCHER_REQUEST_LIST)
                )

                val sessionNoFailure = handleSessionWithNoFailure(
                    session = remoteData.session,
                    sessionData = sessionData,
                    tokenKey = GET_VOUCHER_REQUEST_LIST,
                    appMessage = remoteData.message,
                    error = remoteData.error
                )

                if (sessionNoFailure) {
                    remoteData.data?.let { voucherRequestsDTO ->
                        emit(
                            Resource.Success(
                                data = voucherRequestsDTO.requestDTOList?.map {
                                    it.toVoucherRequest()
                                } ?: listOf(),
                                appMessage = remoteData.message?.toAppMessage()
                            )
                        )
                    }
                }

            } catch (ex: Exception) {
                handleRemoteCallError(ex)
            }
        }

    override fun removeVoucherRequestById(
        requestId: String
    ): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.deleteVoucherRequestById(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = DELETE_VOUCHER_REQUEST,
                    randomString = randomString
                ),
                params = generateVoucherRequestParams(requestId, DELETE_VOUCHER_REQUEST)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = DELETE_VOUCHER_REQUEST,
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

    private fun generateVoucherRequestParams(
        requestId: String,
        tokenKey: String
    ) = mutableMapOf(
        VOUCHER_REQUEST_ID to requestId
    ).also {
        it += getCommonWSParams(sessionData, tokenKey)
    }

    override fun getVoucherStateDataById(
        voucherId: String
    ): Flow<Resource<VoucherStateData>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.fetchVoucherStatusForId(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp = timestamp,
                    methodName = GET_VOUCHER_STATUS,
                    randomString = randomString
                ),
                params = generateVoucherIdParams(voucherId, GET_VOUCHER_STATUS)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = GET_VOUCHER_STATUS,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {
                remoteData.data?.let { getVoucherStateDataDTO ->
                    emit(
                        Resource.Success(
                            data = getVoucherStateDataDTO.voucherStateData.toVoucherStateData(),
                            appMessage = remoteData.message?.toAppMessage()
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            handleRemoteCallError(ex)
        }
    }

    override fun useVoucherById(
        voucherId: String
    ): Flow<Resource<Boolean>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.useVoucher(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(
                    timestamp,
                    USE_VOUCHER,
                    randomString
                ),
                params = generateVoucherIdParams(voucherId, USE_VOUCHER)
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

        } catch (ex: Exception) {
            handleRemoteCallError(ex)
        }
    }

    private fun generateVoucherIdParams(
        voucherId: String,
        tokenKey: String
    ) = mutableMapOf(
        VOUCHER_ID to voucherId
    ).also { it.plusAssign(getCommonWSParams(sessionData, tokenKey)) }
}