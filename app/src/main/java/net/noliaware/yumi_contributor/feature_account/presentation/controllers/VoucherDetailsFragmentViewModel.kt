package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.commun.ApiParameters.VOUCHER_ID
import net.noliaware.yumi_contributor.commun.presentation.EventsHelper
import net.noliaware.yumi_contributor.feature_account.domain.model.Voucher
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStateData
import net.noliaware.yumi_contributor.feature_account.domain.repository.ManagedAccountRepository
import javax.inject.Inject

@HiltViewModel
class VoucherDetailsFragmentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ManagedAccountRepository
) : ViewModel() {

    val getVoucherEventsHelper = EventsHelper<Voucher>()
    val requestSentEventsHelper = EventsHelper<Boolean>()
    val getVoucherStateDataEventsHelper = EventsHelper<VoucherStateData>()

    init {
        savedStateHandle.get<String>(VOUCHER_ID)?.let {
            callGetVoucherById(it)
        }
    }

    private fun callGetVoucherById(voucherId: String) {
        viewModelScope.launch {
            repository.getVoucherById(voucherId).onEach { result ->
                getVoucherEventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }

    fun callSendVoucherRequestWithId(
        voucherId: String,
        voucherRequestTypeId: Int,
        voucherRequestComment: String
    ) {
        viewModelScope.launch {
            repository.sendVoucherRequestWithId(
                voucherId,
                voucherRequestTypeId,
                voucherRequestComment
            ).onEach { result ->
                requestSentEventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }

    fun callGetVoucherStatusById(voucherId: String) {
        viewModelScope.launch {
            repository.getVoucherStateDataById(voucherId).onEach { result ->
                getVoucherStateDataEventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }
}