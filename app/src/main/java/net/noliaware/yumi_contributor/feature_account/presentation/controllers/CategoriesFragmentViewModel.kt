package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.commun.presentation.EventsHelper
import net.noliaware.yumi_contributor.feature_account.domain.repository.ManagedAccountRepository
import net.noliaware.yumi_contributor.feature_account.domain.model.Category
import javax.inject.Inject

@HiltViewModel
class CategoriesFragmentViewModel @Inject constructor(
    private val repository: ManagedAccountRepository
) : ViewModel() {

    val availableCategoriesEventsHelper = EventsHelper<List<Category>>()
    val usedCategoriesEventsHelper = EventsHelper<List<Category>>()
    val cancelledCategoriesEventsHelper = EventsHelper<List<Category>>()

    private val _onDataRefreshedEventFlow = MutableSharedFlow<Unit>()
    val onDataRefreshedEventFlow = _onDataRefreshedEventFlow.asSharedFlow()

    fun callGetAvailableCategories() {
        viewModelScope.launch {
            repository.getAvailableCategories().onEach { result ->
                availableCategoriesEventsHelper.handleResponse(result)
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

    fun callGetCancelledCategories() {
        viewModelScope.launch {
            repository.getCancelledCategories().onEach { result ->
                cancelledCategoriesEventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }

    fun sendDataRefreshedEvent() {
        viewModelScope.launch {
            _onDataRefreshedEventFlow.emit(Unit)
        }
    }
}