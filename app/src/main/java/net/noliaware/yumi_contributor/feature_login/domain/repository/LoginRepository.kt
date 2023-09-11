package net.noliaware.yumi_contributor.feature_login.domain.repository

import kotlinx.coroutines.flow.Flow
import net.noliaware.yumi_contributor.commun.util.Resource
import net.noliaware.yumi_contributor.feature_login.domain.model.AccountData
import net.noliaware.yumi_contributor.feature_login.domain.model.InitData

interface LoginRepository {

    fun getInitData(
        androidId: String,
        deviceId: String?,
        pushToken: String?,
        login: String
    ): Flow<Resource<InitData>>

    fun getAccountData(password: String): Flow<Resource<AccountData>>
}