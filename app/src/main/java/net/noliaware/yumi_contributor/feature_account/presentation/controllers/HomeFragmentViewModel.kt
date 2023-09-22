package net.noliaware.yumi_contributor.feature_account.presentation.controllers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import net.noliaware.yumi_contributor.commun.Args.MANAGED_ACCOUNT
import net.noliaware.yumi_contributor.feature_account.domain.model.ManagedAccount
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    var managedAccount
        get() = savedStateHandle.get<ManagedAccount>(MANAGED_ACCOUNT)
        set(value) = savedStateHandle.set(MANAGED_ACCOUNT, value)
}