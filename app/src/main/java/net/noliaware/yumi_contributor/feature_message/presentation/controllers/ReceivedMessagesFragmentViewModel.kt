package net.noliaware.yumi_contributor.feature_message.presentation.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import net.noliaware.yumi_contributor.feature_message.domain.repository.MessageRepository
import javax.inject.Inject

@HiltViewModel
class ReceivedMessagesFragmentViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {
    fun getMessages() = messageRepository.getReceivedMessageList().cachedIn(viewModelScope)
}