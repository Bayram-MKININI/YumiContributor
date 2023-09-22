package net.noliaware.yumi_contributor.feature_message.presentation.controllers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.commun.ApiParameters.MESSAGE_ID
import net.noliaware.yumi_contributor.commun.presentation.EventsHelper
import net.noliaware.yumi_contributor.feature_message.domain.model.Message
import net.noliaware.yumi_contributor.feature_message.domain.repository.MessageRepository
import javax.inject.Inject

@HiltViewModel
class ReadInboxMailFragmentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: MessageRepository
) : ViewModel() {

    val getMessageEventsHelper = EventsHelper<Message>()
    val deleteMessageEventsHelper = EventsHelper<Boolean>()
    private val messageId get() = savedStateHandle.get<String>(MESSAGE_ID)

    init {
        messageId?.let {
            callGetMessageForId(it)
        }
    }

    private fun callGetMessageForId(messageId: String) {
        viewModelScope.launch {
            repository.getInboxMessageForId(messageId).onEach { result ->
                getMessageEventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }

    fun callDeleteInboxMessageForId() {
        messageId?.let { messageId ->
            viewModelScope.launch {
                repository.deleteInboxMessageForId(messageId).onEach { result ->
                    deleteMessageEventsHelper.handleResponse(result)
                }.launchIn(this)
            }
        }
    }
}