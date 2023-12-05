package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.commun.Args.ACCOUNT_DATA
import net.noliaware.yumi_contributor.commun.presentation.EventsHelper
import net.noliaware.yumi_contributor.feature_account.domain.model.Category
import net.noliaware.yumi_contributor.feature_account.domain.repository.ManagedAccountRepository
import net.noliaware.yumi_contributor.feature_login.domain.model.AccountData
import javax.inject.Inject

@HiltViewModel
class SelectedAccountFragmentViewModel @Inject constructor(
    private val repository: ManagedAccountRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val availableCategoriesEventsHelper = EventsHelper<List<Category>>()
    val cancelledCategoriesEventsHelper = EventsHelper<List<Category>>()
    val usedCategoriesEventsHelper = EventsHelper<List<Category>>()

    var accountData
        get() = savedStateHandle.getStateFlow<AccountData?>(key = ACCOUNT_DATA, initialValue = null).value
        set(value) {
            savedStateHandle[ACCOUNT_DATA] = value
        }

    private val _onAvailableCategoriesListRefreshedEventFlow: MutableSharedFlow<Unit> by lazy {
        MutableSharedFlow()
    }
    val onAvailableCategoriesListRefreshedEventFlow = _onAvailableCategoriesListRefreshedEventFlow.asSharedFlow()

    private val _onUsedCategoriesListRefreshedEventFlow: MutableSharedFlow<Unit> by lazy {
        MutableSharedFlow()
    }
    val onUsedCategoriesListRefreshedEventFlow = _onUsedCategoriesListRefreshedEventFlow.asSharedFlow()

    fun callGetAvailableCategories() {
        viewModelScope.launch {
            repository.getAvailableCategories().onEach { result ->
                availableCategoriesEventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }

    fun callGetCancelledCategories() {
        viewModelScope.launch {
            repository.getCancelledCategories().onEach { result ->
                cancelledCategoriesEventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }

    fun callGetUsedCategories() {
        viewModelScope.launch {
            repository.getUsedCategories().onEach { result ->
                usedCategoriesEventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }

    fun sendCategoriesListsRefreshedEvent() {
        viewModelScope.launch {
            _onAvailableCategoriesListRefreshedEventFlow.emit(Unit)
        }
        viewModelScope.launch {
            _onUsedCategoriesListRefreshedEventFlow.emit(Unit)
        }
    }
}