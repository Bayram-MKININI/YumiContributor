package net.noliaware.yumi_contributor.feature_login.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import net.noliaware.yumi_contributor.commun.ApiParameters.DEVICE_ID
import net.noliaware.yumi_contributor.commun.ApiParameters.LOGIN
import net.noliaware.yumi_contributor.feature_login.data.repository.DataStoreRepositoryImpl.PreferencesKeys.DEVICE_ID_PREF
import net.noliaware.yumi_contributor.feature_login.data.repository.DataStoreRepositoryImpl.PreferencesKeys.LOGIN_PREF
import net.noliaware.yumi_contributor.feature_login.domain.model.UserPreferences
import net.noliaware.yumi_contributor.feature_login.domain.repository.DataStoreRepository
import java.io.IOException
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : DataStoreRepository {

    companion object {
        private const val USER_PREFERENCES_NAME = "user_preferences"
    }

    private object PreferencesKeys {
        val LOGIN_PREF = stringPreferencesKey(LOGIN)
        val DEVICE_ID_PREF = stringPreferencesKey(DEVICE_ID)
    }

    private val Context.dataStore by preferencesDataStore(
        name = USER_PREFERENCES_NAME
    )

    override suspend fun saveLogin(login: String) {
        context.dataStore.edit { preference ->
            preference[LOGIN_PREF] = login
        }
    }

    override suspend fun saveDeviceId(deviceId: String) {
        context.dataStore.edit { preference ->
            preference[DEVICE_ID_PREF] = deviceId
        }
    }

    override fun readUserPreferences(): Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preference ->
            val login = preference[LOGIN_PREF].orEmpty()
            val deviceId = preference[DEVICE_ID_PREF].orEmpty()
            UserPreferences(login, deviceId)
        }

    override suspend fun clearDataStore() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}