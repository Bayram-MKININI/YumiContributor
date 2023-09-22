package net.noliaware.yumi_contributor.feature_message.presentation.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.commun.presentation.EventsHelper
import net.noliaware.yumi_contributor.feature_message.domain.repository.MessageRepository
import javax.inject.Inject

@HiltViewModel
class SendMailFragmentViewModel @Inject constructor(
    private val repository: MessageRepository
) : ViewModel() {

    val messageSentEventsHelper = EventsHelper<Boolean>()

    fun callSendMessage(
        messagePriority: Int? = null,
        messageId: String? = null,
        messageSubjectId: String? = null,
        messageBody: String
    ) {
        viewModelScope.launch {
            repository.sendMessage(messagePriority, messageId, messageSubjectId, messageBody)
                .onEach { result ->
                    messageSentEventsHelper.handleResponse(result)
                }.launchIn(this)
        }
    }
}