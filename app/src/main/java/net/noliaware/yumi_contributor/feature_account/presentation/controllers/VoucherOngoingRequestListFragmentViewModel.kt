package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.commun.ApiParameters
import net.noliaware.yumi_contributor.commun.presentation.EventsHelper
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherRequest
import net.noliaware.yumi_contributor.feature_account.domain.repository.ManagedAccountRepository
import javax.inject.Inject

@HiltViewModel
class VoucherOngoingRequestListFragmentViewModel @Inject constructor(
    private val repository: ManagedAccountRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val getVoucherRequestsEventsHelper = EventsHelper<List<VoucherRequest>>()

    init {
        savedStateHandle.get<String>(ApiParameters.VOUCHER_ID)?.let {
            callGetVoucherById(it)
        }
    }

    private fun callGetVoucherById(voucherId: String) {
        viewModelScope.launch {
            repository.getVoucherRequestListById(voucherId).onEach { result ->
                getVoucherRequestsEventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }
}