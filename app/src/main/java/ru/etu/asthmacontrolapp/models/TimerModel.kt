package ru.etu.asthmacontrolapp.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TimerModel(private val msDelay: Long) : ViewModel() {
    enum class State {
        STOPPED,
        RUNNING,
        PAUSED
    }

    private var job: Job? = null
    private val _msElapsedTime = MutableStateFlow(0L)
    val msElapsedTime = _msElapsedTime.asStateFlow()

    private val _state = MutableStateFlow(State.STOPPED)
    val state = _state.asStateFlow()

    fun start() {
        if (_state.value == State.RUNNING) {
            return
        }
        job?.cancel()
        job = viewModelScope.launch {
            while (isActive) {
                delay(msDelay)
                _msElapsedTime.value += msDelay
            }
        }
        _state.value = State.RUNNING
    }

    fun pause() {
        job?.cancel()
        _state.value = State.PAUSED
    }

    fun stop() {
        job?.cancel()
        _msElapsedTime.value = 0L
        _state.value = State.STOPPED
    }
}