package net.noliaware.yumi_contributor.commun.presentation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import net.noliaware.yumi_contributor.R
import net.noliaware.yumi_contributor.commun.util.*
import net.noliaware.yumi_contributor.commun.util.ViewModelState.DataState
import net.noliaware.yumi_contributor.commun.util.ViewModelState.LoadingState

class EventsHelper<S> {

    private val _stateFlow: MutableStateFlow<ViewModelState<S>> = MutableStateFlow(DataState())
    val stateFlow = _stateFlow.asStateFlow()

    val stateData
        get() = when (stateFlow.value) {
            is DataState -> (stateFlow.value as DataState<S>).data
            is LoadingState -> null
        }

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    suspend fun handleResponse(result: Resource<S>) {
        when (result) {
            is Resource.Success -> {
                result.data?.let {
                    _stateFlow.value = DataState(it)
                }
                result.appMessage?.let {
                    _eventFlow.emit(UIEvent.ShowAppMessage(it))
                }
            }
            is Resource.Loading -> {
                _stateFlow.value = LoadingState()
            }
            is Resource.Error -> {

                result.appMessage?.let {
                    _eventFlow.emit(UIEvent.ShowAppMessage(it))
                    return
                }

                when (result.errorType) {
                    ErrorType.NETWORK_ERROR -> {
                        _eventFlow.emit(
                            UIEvent.ShowError(
                                errorType = ErrorType.NETWORK_ERROR,
                                errorStrRes = R.string.error_no_network
                            )
                        )
                    }
                    ErrorType.SYSTEM_ERROR -> {
                        _eventFlow.emit(
                            UIEvent.ShowError(
                                errorType = ErrorType.SYSTEM_ERROR,
                                errorStrRes = R.string.error_contact_support
                            )
                        )
                    }
                    else -> Unit
                }
            }
        }.exhaustive
    }
}