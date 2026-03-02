package com.oliviermarteaux.a056_bricksbreaker.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.a056_bricksbreaker.domain.User
import com.oliviermarteaux.shared.firebase.authentication.data.repository.UserRepository
import com.oliviermarteaux.shared.firebase.authentication.ui.AuthUserViewModel
import com.oliviermarteaux.shared.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val userRepository: UserRepository,
    isOnlineFlow: Flow<Boolean>,
    log: Logger
) : AuthUserViewModel(
    userRepository = userRepository,
    isOnlineFlow = isOnlineFlow,
    log = log,
) {
    var speed by mutableFloatStateOf(5f)
        private set

    var timeElapsed by mutableLongStateOf(0L)
        private set

    private var isTimerRunning = false
    private var timerJob: Job? = null

    fun updateSpeed(newSpeed: Float) {
        speed = newSpeed
    }

    fun startTime() {
        timeElapsed = 0L
        isTimerRunning = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isTimerRunning) {
                delay(1000)
                timeElapsed++
            }
        }
    }

    fun stopTime() {
        isTimerRunning = false
        timerJob?.cancel()
    }

    fun updateScore() {
        //userRepository.updateScore(timeElapsed)
    }

    fun getAllScores(): List<User> {
        return emptyList()
        //return userRepository.getAllScores()
    }
}