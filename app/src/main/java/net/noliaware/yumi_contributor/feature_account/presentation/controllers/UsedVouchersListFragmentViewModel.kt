package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import net.noliaware.yumi_contributor.commun.CATEGORY_ID
import net.noliaware.yumi_contributor.commun.CATEGORY_LABEL
import net.noliaware.yumi_contributor.feature_account.data.repository.ManagedAccountRepository
import net.noliaware.yumi_contributor.feature_profile.data.repository.ProfileRepository
import javax.inject.Inject

@HiltViewModel
class UsedVouchersListFragmentViewModel @Inject constructor(
    private val repository: ManagedAccountRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val selectedCategoryId get() = savedStateHandle.get<String>(CATEGORY_ID).orEmpty()
    val categoryLabel get() = savedStateHandle.get<String>(CATEGORY_LABEL).orEmpty()

    fun getVouchers() = repository.getUsedVoucherList(selectedCategoryId).cachedIn(viewModelScope)
}