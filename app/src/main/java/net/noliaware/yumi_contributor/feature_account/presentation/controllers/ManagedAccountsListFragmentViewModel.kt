package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import net.noliaware.yumi_contributor.feature_account.data.repository.ManagedAccountRepository
import javax.inject.Inject

@HiltViewModel
class ManagedAccountsListFragmentViewModel @Inject constructor(
    private val repository: ManagedAccountRepository
) : ViewModel() {
    fun getManagedAccounts() = repository.getManagedAccountList().cachedIn(viewModelScope)
}