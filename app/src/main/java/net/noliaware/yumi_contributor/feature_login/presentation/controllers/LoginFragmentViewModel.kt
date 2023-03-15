package net.noliaware.yumi_contributor.feature_login.presentation.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.commun.presentation.EventsHelper
import net.noliaware.yumi_contributor.commun.util.ViewModelState
import net.noliaware.yumi_contributor.commun.util.ViewModelState.DataState
import net.noliaware.yumi_contributor.feature_login.data.repository.DataStoreRepository
import net.noliaware.yumi_contributor.feature_login.data.repository.LoginRepository
import net.noliaware.yumi_contributor.feature_login.domain.model.AccountData
import net.noliaware.yumi_contributor.feature_login.domain.model.InitData
import net.noliaware.yumi_contributor.feature_login.domain.model.UserPreferences
import org.json.JSONArray
import javax.inject.Inject

@HiltViewModel
class LoginFragmentViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _prefsStateFlow: MutableStateFlow<ViewModelState<UserPreferences>> =
        MutableStateFlow(DataState())
    val prefsStateFlow = _prefsStateFlow.asStateFlow()

    val prefsStateData
        get() = when (prefsStateFlow.value) {
            is DataState -> (prefsStateFlow.value as DataState<UserPreferences>).data
            is ViewModelState.LoadingState -> null
        }

    val initEventsHelper = EventsHelper<InitData>()
    val accountDataEventsHelper = EventsHelper<AccountData>()

    init {
        callReadPreferences()
    }

    private fun callReadPreferences() {
        viewModelScope.launch {
            dataStoreRepository.readUserPreferences().onEach { userPreferences ->
                _prefsStateFlow.value = DataState(userPreferences)
            }.launchIn(this)
        }
    }

    fun saveLoginPreferences(login: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (prefsStateData?.login != login) {
                dataStoreRepository.clearDataStore()
                dataStoreRepository.saveLogin(login)
            }
        }
    }

    fun saveDeviceIdPreferences(deviceId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveDeviceId(deviceId)
        }
    }

    fun callInitWebservice(androidId: String, deviceId: String?, login: String) {
        viewModelScope.launch {
            repository.getInitData(androidId, deviceId, login).onEach { result ->
                initEventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }

    fun callConnectWebserviceWithIndexes(passwordIndexes: List<Int>) {
        viewModelScope.launch {
            repository.getAccountData(JSONArray(passwordIndexes).toString()).onEach { result ->
                accountDataEventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }
}