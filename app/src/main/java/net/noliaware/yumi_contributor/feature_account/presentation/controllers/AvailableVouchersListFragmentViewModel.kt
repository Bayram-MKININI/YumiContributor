package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import android.provider.MediaStore.Video.VideoColumns.CATEGORY
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import net.noliaware.yumi_contributor.commun.DATA_SHOULD_REFRESH
import net.noliaware.yumi_contributor.feature_account.data.repository.ManagedAccountRepository
import net.noliaware.yumi_contributor.feature_account.domain.model.Category
import javax.inject.Inject

@HiltViewModel
class AvailableVouchersListFragmentViewModel @Inject constructor(
    private val repository: ManagedAccountRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val selectedCategory get() = savedStateHandle.get<Category>(CATEGORY)

    var dataShouldRefresh
        get() = savedStateHandle.get<Boolean>(DATA_SHOULD_REFRESH)
        set(value) = savedStateHandle.set(DATA_SHOULD_REFRESH, value)

    fun getVouchers() = selectedCategory?.categoryId?.let {
        repository.getAvailableVoucherList(it).cachedIn(viewModelScope)
    }
}