package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.commun.presentation.EventsHelper
import net.noliaware.yumi_contributor.feature_account.data.repository.ManagedAccountRepository
import net.noliaware.yumi_contributor.feature_account.domain.model.Category
import javax.inject.Inject

@HiltViewModel
class CancelledCategoriesFragmentViewModel @Inject constructor(
    private val managedAccountRepository: ManagedAccountRepository
) : ViewModel() {

    val eventsHelper = EventsHelper<List<Category>>()

    init {
        callGetUsedCategories()
    }

    private fun callGetUsedCategories() {
        viewModelScope.launch {
            managedAccountRepository.getUsedCategories().onEach { result ->
                eventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }
}