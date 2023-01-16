package net.noliaware.yumi_contributor.feature_message.presentation.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import net.noliaware.yumi_contributor.feature_message.data.repository.MessageRepository
import javax.inject.Inject

@HiltViewModel
class SentMessagesFragmentViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {
    fun getMessages() = messageRepository.getSentMessageList().cachedIn(viewModelScope)
}