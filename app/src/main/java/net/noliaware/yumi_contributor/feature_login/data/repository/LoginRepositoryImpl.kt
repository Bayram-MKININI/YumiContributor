package net.noliaware.yumi_contributor.feature_login.data.repository

import android.os.Build
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.noliaware.yumi_contributor.BuildConfig
import net.noliaware.yumi_contributor.commun.ApiConstants.CONNECT
import net.noliaware.yumi_contributor.commun.ApiConstants.DELETE_INBOX_MESSAGE
import net.noliaware.yumi_contributor.commun.ApiConstants.DELETE_OUTBOX_MESSAGE
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_ACCOUNT
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_ALERT_LIST
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_AVAILABLE_DATA_PER_CATEGORY
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_AVAILABLE_VOUCHER_LIST_BY_CATEGORY
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_BACK_OFFICE_SIGN_IN_CODE
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_CANCELLED_DATA_PER_CATEGORY
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_CANCELLED_VOUCHER_LIST_BY_CATEGORY
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_FILTER_USERS_LIST
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_INBOX_MESSAGE
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_INBOX_MESSAGE_LIST
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_MANAGED_ACCOUNT_LIST
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_OUTBOX_MESSAGE
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_OUTBOX_MESSAGE_LIST
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_SINGLE_MANAGED_ACCOUNT
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_USED_DATA_PER_CATEGORY
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_USED_VOUCHER_LIST_BY_CATEGORY
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_VOUCHER
import net.noliaware.yumi_contributor.commun.ApiConstants.GET_VOUCHER_STATUS
import net.noliaware.yumi_contributor.commun.ApiConstants.INIT
import net.noliaware.yumi_contributor.commun.ApiConstants.SELECT_USER
import net.noliaware.yumi_contributor.commun.ApiConstants.SEND_MESSAGE
import net.noliaware.yumi_contributor.commun.ApiConstants.SET_PRIVACY_POLICY_READ_STATUS
import net.noliaware.yumi_contributor.commun.ApiConstants.USE_VOUCHER
import net.noliaware.yumi_contributor.commun.ApiParameters.APP_VERSION
import net.noliaware.yumi_contributor.commun.ApiParameters.DEVICE_ID
import net.noliaware.yumi_contributor.commun.ApiParameters.DEVICE_LABEL
import net.noliaware.yumi_contributor.commun.ApiParameters.DEVICE_OS
import net.noliaware.yumi_contributor.commun.ApiParameters.DEVICE_TYPE
import net.noliaware.yumi_contributor.commun.ApiParameters.DEVICE_UUID
import net.noliaware.yumi_contributor.commun.ApiParameters.LOGIN
import net.noliaware.yumi_contributor.commun.ApiParameters.PASSWORD
import net.noliaware.yumi_contributor.commun.ApiParameters.PUSH_TOKEN
import net.noliaware.yumi_contributor.commun.data.remote.RemoteApi
import net.noliaware.yumi_contributor.commun.data.remote.dto.SessionDTO
import net.noliaware.yumi_contributor.commun.domain.model.SessionData
import net.noliaware.yumi_contributor.commun.util.Resource
import net.noliaware.yumi_contributor.commun.util.currentTimeInMillis
import net.noliaware.yumi_contributor.commun.util.generateToken
import net.noliaware.yumi_contributor.commun.util.getCommonWSParams
import net.noliaware.yumi_contributor.commun.util.handleRemoteCallError
import net.noliaware.yumi_contributor.commun.util.handleSessionWithNoFailure
import net.noliaware.yumi_contributor.commun.util.randomString
import net.noliaware.yumi_contributor.feature_login.domain.model.AccountData
import net.noliaware.yumi_contributor.feature_login.domain.model.InitData
import net.noliaware.yumi_contributor.feature_login.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val api: RemoteApi,
    private val sessionData: SessionData
) : LoginRepository {

    override fun getInitData(
        androidId: String,
        deviceId: String?,
        pushToken: String?,
        login: String
    ): Flow<Resource<InitData>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.fetchInitData(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(timestamp, INIT, randomString),
                params = generateInitParams(
                    androidId = androidId,
                    deviceId = deviceId,
                    pushToken = pushToken,
                    login = login
                )
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = CONNECT,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {
                remoteData.data?.let { initDTO ->
                    sessionData.login = login
                    sessionData.deviceId = initDTO.deviceId
                    emit(
                        Resource.Success(
                            data = initDTO.toInitData(),
                            appMessage = remoteData.message?.toAppMessage()
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            handleRemoteCallError(ex)
        }
    }

    private fun generateInitParams(
        androidId: String,
        deviceId: String?,
        pushToken: String?,
        login: String
    ): Map<String, String> {

        val parameters = mutableMapOf(
            LOGIN to login,
            APP_VERSION to BuildConfig.VERSION_NAME
        )

        if (deviceId.isNullOrEmpty()) {
            parameters[DEVICE_TYPE] = "S"
            parameters[DEVICE_OS] = "ANDROID"
            parameters[DEVICE_UUID] = androidId
            parameters[DEVICE_LABEL] = Build.MODEL
        } else {
            parameters[DEVICE_ID] = deviceId
        }

        pushToken?.let {
            parameters[PUSH_TOKEN] = pushToken
        }

        return parameters
    }

    override fun getAccountData(password: String): Flow<Resource<AccountData>> = flow {

        emit(Resource.Loading())

        try {
            val timestamp = currentTimeInMillis()
            val randomString = randomString()

            val remoteData = api.fetchAccountDataForPassword(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(timestamp, CONNECT, randomString),
                params = generateGetAccountParams(password, CONNECT)
            )

            val sessionNoFailure = handleSessionWithNoFailure(
                session = remoteData.session,
                sessionData = sessionData,
                tokenKey = CONNECT,
                appMessage = remoteData.message,
                error = remoteData.error
            )

            if (sessionNoFailure) {

                remoteData.session?.let { sessionDTO ->
                    sessionData.fillMapWithInitialToken(sessionDTO)
                }

                remoteData.data?.let { accountDataDTO ->
                    emit(
                        Resource.Success(
                            data = accountDataDTO.toAccountData(),
                            appMessage = remoteData.message?.toAppMessage()
                        )
                    )
                }
            }

        } catch (ex: Exception) {
            handleRemoteCallError(ex)
        }
    }

    private fun generateGetAccountParams(password: String, tokenKey: String) = mutableMapOf(
        PASSWORD to password
    ).also { it.plusAssign(getCommonWSParams(sessionData, tokenKey)) }

    private fun SessionData.fillMapWithInitialToken(sessionDTO: SessionDTO) {
        this.sessionTokens[SET_PRIVACY_POLICY_READ_STATUS] = sessionDTO.sessionToken
        this.sessionTokens[GET_FILTER_USERS_LIST] = sessionDTO.sessionToken
        this.sessionTokens[GET_SINGLE_MANAGED_ACCOUNT] = sessionDTO.sessionToken
        this.sessionTokens[GET_MANAGED_ACCOUNT_LIST] = sessionDTO.sessionToken
        this.sessionTokens[SELECT_USER] = sessionDTO.sessionToken
        this.sessionTokens[GET_AVAILABLE_DATA_PER_CATEGORY] = sessionDTO.sessionToken
        this.sessionTokens[GET_AVAILABLE_VOUCHER_LIST_BY_CATEGORY] = sessionDTO.sessionToken
        this.sessionTokens[GET_USED_DATA_PER_CATEGORY] = sessionDTO.sessionToken
        this.sessionTokens[GET_USED_VOUCHER_LIST_BY_CATEGORY] = sessionDTO.sessionToken
        this.sessionTokens[GET_CANCELLED_DATA_PER_CATEGORY] = sessionDTO.sessionToken
        this.sessionTokens[GET_CANCELLED_VOUCHER_LIST_BY_CATEGORY] = sessionDTO.sessionToken
        this.sessionTokens[GET_VOUCHER] = sessionDTO.sessionToken
        this.sessionTokens[GET_VOUCHER_STATUS] = sessionDTO.sessionToken
        this.sessionTokens[USE_VOUCHER] = sessionDTO.sessionToken
        this.sessionTokens[GET_ACCOUNT] = sessionDTO.sessionToken
        this.sessionTokens[GET_BACK_OFFICE_SIGN_IN_CODE] = sessionDTO.sessionToken
        this.sessionTokens[GET_ALERT_LIST] = sessionDTO.sessionToken
        this.sessionTokens[GET_INBOX_MESSAGE_LIST] = sessionDTO.sessionToken
        this.sessionTokens[GET_INBOX_MESSAGE] = sessionDTO.sessionToken
        this.sessionTokens[GET_OUTBOX_MESSAGE_LIST] = sessionDTO.sessionToken
        this.sessionTokens[GET_OUTBOX_MESSAGE] = sessionDTO.sessionToken
        this.sessionTokens[SEND_MESSAGE] = sessionDTO.sessionToken
        this.sessionTokens[DELETE_INBOX_MESSAGE] = sessionDTO.sessionToken
        this.sessionTokens[DELETE_OUTBOX_MESSAGE] = sessionDTO.sessionToken
    }
}