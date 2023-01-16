package net.noliaware.yumi_contributor.feature_login.data.repository

import android.os.Build
import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.noliaware.yumi_contributor.BuildConfig
import net.noliaware.yumi_contributor.commun.*
import net.noliaware.yumi_contributor.commun.data.remote.RemoteApi
import net.noliaware.yumi_contributor.commun.data.remote.dto.SessionDTO
import net.noliaware.yumi_contributor.commun.domain.model.SessionData
import net.noliaware.yumi_contributor.commun.util.*
import net.noliaware.yumi_contributor.feature_login.domain.model.AccountData
import net.noliaware.yumi_contributor.feature_login.domain.model.InitData
import okio.IOException
import retrofit2.HttpException
import java.util.*
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val api: RemoteApi,
    private val sessionData: SessionData
) : LoginRepository {

    override fun getInitData(
        androidId: String,
        deviceId: String?,
        login: String
    ): Flow<Resource<InitData>> = flow {

        emit(Resource.Loading())

        try {

            val timestamp = System.currentTimeMillis().toString()
            val randomString = UUID.randomUUID().toString()

            val remoteData = api.fetchInitData(
                timestamp = timestamp,
                saltString = randomString,
                token = generateToken(timestamp, INIT, randomString),
                params = generateInitParams(androidId, deviceId, login)
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
                    emit(Resource.Success(data = initDTO.toInitData()))
                }
            }

        } catch (ex: HttpException) {
            emit(Resource.Error(errorType = ErrorType.SYSTEM_ERROR))
        } catch (ex: IOException) {
            emit(Resource.Error(errorType = ErrorType.NETWORK_ERROR))
        }
    }

    private fun generateInitParams(
        androidId: String,
        deviceId: String?,
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

        return parameters
    }

    override fun getAccountData(password: String): Flow<Resource<AccountData>> = flow {

        emit(Resource.Loading())

        try {

            val timestamp = System.currentTimeMillis().toString()
            val randomString = UUID.randomUUID().toString()

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

        } catch (ex: HttpException) {
            emit(Resource.Error(errorType = ErrorType.SYSTEM_ERROR))
        } catch (ex: IOException) {
            emit(Resource.Error(errorType = ErrorType.NETWORK_ERROR))
        }
    }

    private fun generateGetAccountParams(password: String, tokenKey: String) = mutableMapOf(
        PASSWORD to password
    ).also { it.plusAssign(getCommonWSParams(sessionData, tokenKey)) }

    private fun SessionData.fillMapWithInitialToken(sessionDTO: SessionDTO) {
        this.sessionTokens[GET_MANAGED_ACCOUNT_LIST] = sessionDTO.sessionToken
        this.sessionTokens[SELECT_USER] = sessionDTO.sessionToken
        this.sessionTokens[GET_AVAILABLE_VOUCHER_LIST_BY_CATEGORY] = sessionDTO.sessionToken
        this.sessionTokens[GET_VOUCHER] = sessionDTO.sessionToken
        this.sessionTokens[GET_VOUCHER_STATUS] = sessionDTO.sessionToken
        this.sessionTokens[USE_VOUCHER] = sessionDTO.sessionToken
        this.sessionTokens[GET_ACCOUNT] = sessionDTO.sessionToken
        this.sessionTokens[GET_BACK_OFFICE_SIGN_IN_CODE] = sessionDTO.sessionToken
        this.sessionTokens[GET_DATA_PER_CATEGORY] = sessionDTO.sessionToken
        this.sessionTokens[GET_USED_VOUCHER_LIST_BY_CATEGORY] = sessionDTO.sessionToken
        this.sessionTokens[GET_ALERT_LIST] = sessionDTO.sessionToken
        this.sessionTokens[GET_INBOX_MESSAGE_LIST] = sessionDTO.sessionToken
        this.sessionTokens[GET_INBOX_MESSAGE] = sessionDTO.sessionToken
        this.sessionTokens[GET_OUTBOX_MESSAGE_LIST] = sessionDTO.sessionToken
        this.sessionTokens[GET_OUTBOX_MESSAGE] = sessionDTO.sessionToken
        this.sessionTokens[SEND_MESSAGE] = sessionDTO.sessionToken
    }
}