package com.oliviermarteaux.a056_bricksbreaker.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.shared.firebase.authentication.data.repository.UserRepository
import com.oliviermarteaux.shared.firebase.authentication.domain.model.User
import com.oliviermarteaux.shared.firebase.authentication.ui.AuthUserViewModel
import com.oliviermarteaux.shared.ui.UiState
import com.oliviermarteaux.shared.utils.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val userRepository: UserRepository,
    isOnlineFlow: Flow<Boolean>,
    private val log: Logger
) : AuthUserViewModel(
    userRepository = userRepository,
    isOnlineFlow = isOnlineFlow,
    log = log,
) {
    var level: LevelUiState by mutableStateOf(LevelUiState.LEVEL1)
        private set
    fun nextLevel() {
        level = when (level) {
            LevelUiState.LEVEL1 -> LevelUiState.LEVEL2
            LevelUiState.LEVEL2 -> LevelUiState.LEVEL3
            LevelUiState.LEVEL3 -> LevelUiState.LEVEL4
            LevelUiState.LEVEL4 -> LevelUiState.LEVEL5
            LevelUiState.LEVEL5 -> LevelUiState.LEVEL6
            LevelUiState.LEVEL6 -> LevelUiState.LEVEL7
            LevelUiState.LEVEL7 -> LevelUiState.LEVEL8
            LevelUiState.LEVEL8 -> LevelUiState.LEVEL9
            LevelUiState.LEVEL9 -> LevelUiState.LEVEL10
            LevelUiState.LEVEL10 -> LevelUiState.LEVEL1
        }
    }
    var speed by mutableFloatStateOf(5f)
        private set
    var timeElapsed by mutableLongStateOf(0L)
        private set
    var pseudo: String by mutableStateOf("")
        private set

    private var isTimerRunning = false
    private var timerJob: Job? = null

    fun onPseudoChange(newPseudo: String) {
        pseudo = newPseudo
    }

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

    fun updatePseudo(){
        if (pseudo.isBlank()) return
        updateUser(currentUser?.copy(pseudo = pseudo)?: User())
        log.d("GameViewModel: updatePseudo(): pseudo updated to ${currentUser?.pseudo}")
    }

    fun updateScore() {
        currentUser?.let {
            val newScore = when (it.score) {
                -1L -> timeElapsed
                else -> minOf(timeElapsed, it.score)
            }
            updateUser(it.copy(score = newScore))
            log.d("GameViewModel: updateScore(): score updated to ${currentUser?.score}")
        }
    }
}