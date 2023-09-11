package net.noliaware.yumi_contributor.feature_message.presentation.controllers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import net.noliaware.yumi_contributor.commun.ApiParameters.MESSAGE
import net.noliaware.yumi_contributor.commun.ApiParameters.MESSAGE_SUBJECTS_DATA
import net.noliaware.yumi_contributor.commun.presentation.EventsHelper
import net.noliaware.yumi_contributor.feature_login.domain.model.MessageSubject
import net.noliaware.yumi_contributor.feature_message.domain.repository.MessageRepository
import net.noliaware.yumi_contributor.feature_message.domain.model.Message
import javax.inject.Inject

@HiltViewModel
class SendMailFragmentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: MessageRepository
) : ViewModel() {

    val messageSubjects get() = savedStateHandle.get<List<MessageSubject>>(MESSAGE_SUBJECTS_DATA)
    val message get() = savedStateHandle.get<Message>(MESSAGE)
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