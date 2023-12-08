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
    private val categoryRepository: ManagedAccountRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val voucherId get() = savedStateHandle.get<String>(ApiParameters.VOUCHER_ID)
    val getVoucherRequestsEventsHelper = EventsHelper<List<VoucherRequest>>()
    val deleteVoucherRequestEventsHelper = EventsHelper<Boolean>()

    init {
        callGetVoucherRequestList()
    }

    fun callGetVoucherRequestList() {
        voucherId?.let {
            viewModelScope.launch {
                categoryRepository.getVoucherRequestListById(it).onEach { result ->
                    getVoucherRequestsEventsHelper.handleResponse(result)
                }.launchIn(this)
            }
        }
    }

    fun callRemoveVoucherRequestById(requestId: String) {
        viewModelScope.launch {
            categoryRepository.removeVoucherRequestById(requestId).onEach { result ->
                deleteVoucherRequestEventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }
}