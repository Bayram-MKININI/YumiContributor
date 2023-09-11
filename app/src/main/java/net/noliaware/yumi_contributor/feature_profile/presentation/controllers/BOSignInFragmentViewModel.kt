package net.noliaware.yumi_contributor.feature_profile.presentation.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay
import net.noliaware.yumi_contributor.commun.presentation.EventsHelper
import net.noliaware.yumi_contributor.feature_profile.domain.repository.ProfileRepository
import net.noliaware.yumi_contributor.feature_profile.domain.model.BOSignIn
import net.noliaware.yumi_contributor.feature_profile.domain.model.TimerState
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class BOSignInFragmentViewModel @Inject constructor(
    private val repository: ProfileRepository
) : ViewModel() {

    val eventsHelper = EventsHelper<BOSignIn>()
    private var _timerStateFlow = MutableStateFlow(TimerState())
    val timerStateFlow: StateFlow<TimerState> = _timerStateFlow

    init {
        callGetBackOfficeSignInCode()
    }

    private fun callGetBackOfficeSignInCode() {
        viewModelScope.launch {
            repository.getBackOfficeSignInCode().onEach { result ->
                eventsHelper.handleResponse(result)
            }.launchIn(this)
        }
    }

    fun startTimerWithPeriod(totalSeconds: Int) {
        viewModelScope.launch {
            initTimer(totalSeconds).launchIn(this)
        }
    }

    private fun initTimer(totalSeconds: Int): Flow<TimerState> =
        (totalSeconds - 1 downTo 0).asFlow() // Emit total - 1 because the first was emitted onStart
            .onEach { delay(Duration.ofSeconds(1)) } // Each second later emit a number
            .onStart { _timerStateFlow.emit(TimerState(totalSeconds)) } // Emit total seconds immediately
            .conflate() // In case the operation onTick takes some time, conflate keeps the time ticking separately
            .transform { remainingSeconds: Int ->
                _timerStateFlow.emit(TimerState(remainingSeconds))
            }
}