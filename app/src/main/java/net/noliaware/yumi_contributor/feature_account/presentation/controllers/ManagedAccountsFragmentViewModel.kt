package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.commun.Args.ACCOUNT_DATA
import net.noliaware.yumi_contributor.commun.presentation.EventsHelper
import net.noliaware.yumi_contributor.feature_account.domain.model.ManagedAccount
import net.noliaware.yumi_contributor.feature_account.domain.model.SelectableData
import net.noliaware.yumi_contributor.feature_account.domain.model.SelectableData.AssignedData
import net.noliaware.yumi_contributor.feature_account.domain.model.SelectableData.SelectedData
import net.noliaware.yumi_contributor.feature_account.domain.repository.ManagedAccountRepository
import net.noliaware.yumi_contributor.feature_login.domain.model.AccountData
import javax.inject.Inject

@HiltViewModel
class ManagedAccountsFragmentViewModel @Inject constructor(
    private val repository: ManagedAccountRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val accountData get() = savedStateHandle.get<AccountData>(ACCOUNT_DATA)
    val getFilteredAccountEventsHelper = EventsHelper<ManagedAccount>()
    val getUsersAutocompleteListEventsHelper = EventsHelper<List<ManagedAccount>>()
    val selectAccountEventsHelper = EventsHelper<String>()

    private val _managedAccountFlow: MutableStateFlow<SelectableData<ManagedAccount>> by lazy {
        MutableStateFlow(AssignedData(null))
    }
    val managedAccountFlow = _managedAccountFlow.asStateFlow()

    private val _onBackEventFlow: MutableSharedFlow<Unit> by lazy {
        MutableSharedFlow()
    }
    val onBackEventFlow = _onBackEventFlow.asSharedFlow()

    init {
        callGetAllAutocompleteUsers()
    }

    fun setInitManagedAccount(managedAccount: ManagedAccount) {
        _managedAccountFlow.value = AssignedData(managedAccount)
    }

    fun setSelectedManagedAccount(managedAccount: ManagedAccount) {
        _managedAccountFlow.value = SelectedData(managedAccount)
        callSelectAccountForId(managedAccount.login)
    }

    fun getSelectedManagedAccount() = when (val selectedAccount = _managedAccountFlow.value) {
        is AssignedData -> selectedAccount.data
        is SelectedData -> selectedAccount.data
    }

    private fun callGetAllAutocompleteUsers() {
        viewModelScope.launch {
            repository.getFilterUsers().onEach { result ->
                getUsersAutocompleteListEventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }

    fun callManagedAccountForUserId(userId: String) {
        viewModelScope.launch {
            repository.getManagedAccountForId(userId).onEach { result ->
                getFilteredAccountEventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }

    fun getManagedAccounts() = repository.getManagedAccountList().cachedIn(viewModelScope)

    private fun callSelectAccountForId(accountId: String) {
        viewModelScope.launch {
            repository.selectManagedAccountForId(accountId).onEach { result ->
                selectAccountEventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }

    fun sendBackButtonClickedEvent() {
        viewModelScope.launch {
            _onBackEventFlow.emit(Unit)
        }
    }

    fun resetFilteredManagedAccount() {
        getFilteredAccountEventsHelper.resetStateData()
    }

    fun resetSelectedManagedAccount() {
        _managedAccountFlow.value = SelectedData(null)
    }
}