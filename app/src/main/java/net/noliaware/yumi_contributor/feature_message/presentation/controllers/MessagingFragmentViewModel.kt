package net.noliaware.yumi_contributor.feature_message.presentation.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.feature_message.domain.repository.MessageRepository
import javax.inject.Inject

@HiltViewModel
class MessagingFragmentViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {

    private val _onReceivedListRefreshedEventFlow = MutableSharedFlow<Unit>()
    val onReceivedListRefreshedEventFlow = _onReceivedListRefreshedEventFlow.asSharedFlow()

    private val _onSentListRefreshedEventFlow = MutableSharedFlow<Unit>()
    val onSentListRefreshedEventFlow = _onSentListRefreshedEventFlow.asSharedFlow()

    fun getReceivedMessages() = messageRepository.getReceivedMessageList().cachedIn(viewModelScope)
    fun getSentMessages() = messageRepository.getSentMessageList().cachedIn(viewModelScope)

    fun sendReceivedListRefreshedEvent() {
        viewModelScope.launch {
            _onReceivedListRefreshedEventFlow.emit(Unit)
        }
    }

    fun sendSentListRefreshedEvent() {
        viewModelScope.launch {
            _onSentListRefreshedEventFlow.emit(Unit)
        }
    }
}