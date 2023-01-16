package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.commun.MANAGED_ACCOUNT
import net.noliaware.yumi_contributor.commun.presentation.EventsHelper
import net.noliaware.yumi_contributor.feature_account.data.repository.ManagedAccountRepository
import net.noliaware.yumi_contributor.feature_account.domain.model.Category
import net.noliaware.yumi_contributor.feature_account.domain.model.ManagedAccount
import javax.inject.Inject

@HiltViewModel
class ManagedAccountFragmentViewModel @Inject constructor(
    private val repository: ManagedAccountRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val managedAccount get() = savedStateHandle.get<ManagedAccount>(MANAGED_ACCOUNT)
    val selectAccountEventsHelper = EventsHelper<String>()
    val categoriesEventsHelper = EventsHelper<List<Category>>()

    init {
        managedAccount?.let {
            callGetCategories()
        }
    }

    fun callSelectAccountForId(accountId: String) {
        viewModelScope.launch {
            repository.selectManagedAccountForId(accountId).onEach { result ->
                selectAccountEventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }

    fun callGetCategories() {
        viewModelScope.launch {
            repository.getCategories().onEach { result ->
                categoriesEventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }
}