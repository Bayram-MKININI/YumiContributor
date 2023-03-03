package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import net.noliaware.yumi_contributor.commun.CATEGORY
import net.noliaware.yumi_contributor.feature_account.data.repository.ManagedAccountRepository
import net.noliaware.yumi_contributor.feature_account.domain.model.Category
import javax.inject.Inject

@HiltViewModel
class CancelledVouchersListFragmentViewModel @Inject constructor(
    private val repository: ManagedAccountRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val selectedCategory get() = savedStateHandle.get<Category>(CATEGORY)

    fun getVouchers() = selectedCategory?.categoryId?.let {
        repository.getCancelledVoucherList(it).cachedIn(viewModelScope)
    }
}