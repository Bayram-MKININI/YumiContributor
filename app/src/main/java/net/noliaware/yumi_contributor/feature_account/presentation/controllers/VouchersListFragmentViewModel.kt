package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import net.noliaware.yumi_contributor.commun.CATEGORY_ID
import net.noliaware.yumi_contributor.commun.CATEGORY_LABEL
import net.noliaware.yumi_contributor.commun.DATA_SHOULD_REFRESH
import net.noliaware.yumi_contributor.feature_account.data.repository.ManagedAccountRepository
import javax.inject.Inject

@HiltViewModel
class VouchersListFragmentViewModel @Inject constructor(
    private val repository: ManagedAccountRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val selectedCategoryId get() = savedStateHandle.get<String>(CATEGORY_ID).orEmpty()
    val categoryLabel get() = savedStateHandle.get<String>(CATEGORY_LABEL).orEmpty()
    var dataShouldRefresh
        get() = savedStateHandle.get<Boolean>(DATA_SHOULD_REFRESH)
        set(value) = savedStateHandle.set(DATA_SHOULD_REFRESH, value)

    fun getVouchers() = repository.getVoucherList(selectedCategoryId).cachedIn(viewModelScope)
}