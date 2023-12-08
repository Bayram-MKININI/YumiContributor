package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.commun.ApiParameters.VOUCHER_ID
import net.noliaware.yumi_contributor.commun.Args.DATA_SHOULD_REFRESH
import net.noliaware.yumi_contributor.commun.presentation.EventsHelper
import net.noliaware.yumi_contributor.feature_account.domain.model.Voucher
import net.noliaware.yumi_contributor.feature_account.domain.model.VoucherStateData
import net.noliaware.yumi_contributor.feature_account.domain.repository.ManagedAccountRepository
import javax.inject.Inject

@HiltViewModel
class VoucherDetailsFragmentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ManagedAccountRepository
) : ViewModel() {

    private val voucherId get() = savedStateHandle.get<String>(VOUCHER_ID)
    val getVoucherEventsHelper = EventsHelper<Voucher>()
    val requestSentEventsHelper = EventsHelper<Boolean>()
    val getVoucherStateDataEventsHelper = EventsHelper<VoucherStateData>()

    var voucherListShouldRefresh
        get() = savedStateHandle.getStateFlow(key = DATA_SHOULD_REFRESH, initialValue = false).value
        set(value) {
            savedStateHandle[DATA_SHOULD_REFRESH] = value
        }

    init {
        callGetVoucher()
    }

    fun callGetVoucher() {
        voucherId?.let { voucherId ->
            viewModelScope.launch {
                repository.getVoucherById(voucherId).onEach { result ->
                    getVoucherEventsHelper.handleResponse(result)
                }.launchIn(this)
            }
        }
    }

    fun callSendVoucherRequestWithTypeId(
        voucherRequestTypeId: Int,
        voucherRequestComment: String
    ) {
        voucherId?.let { voucherId ->
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
    }

    fun callGetVoucherStatusById() {
        voucherId?.let { voucherId ->
            viewModelScope.launch {
                repository.getVoucherStateDataById(voucherId).onEach { result ->
                    getVoucherStateDataEventsHelper.handleResponse(result)
                }.launchIn(this)
            }
        }
    }
}