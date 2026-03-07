package com.oliviermarteaux.a056_bricksbreaker.ui

import android.R.attr.level
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.shared.firebase.authentication.data.repository.UserRepository
import com.oliviermarteaux.shared.firebase.authentication.domain.model.GameLevel
import com.oliviermarteaux.shared.firebase.authentication.domain.model.GameLevelStat
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

    var currentLevel: GameLevel by mutableStateOf(GameLevel.LEVEL1)
        private set
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

    fun setNextLevel() {
        currentLevel = when (currentLevel) {
            GameLevel.LEVEL1 -> GameLevel.LEVEL2
            GameLevel.LEVEL2 -> GameLevel.LEVEL3
            GameLevel.LEVEL3 -> GameLevel.LEVEL4
            GameLevel.LEVEL4 -> GameLevel.LEVEL5
            GameLevel.LEVEL5 -> GameLevel.LEVEL6
            GameLevel.LEVEL6 -> GameLevel.LEVEL7
            GameLevel.LEVEL7 -> GameLevel.LEVEL8
            GameLevel.LEVEL8 -> GameLevel.LEVEL9
            GameLevel.LEVEL9 -> GameLevel.LEVEL10
            GameLevel.LEVEL10 -> GameLevel.LEVEL1
            GameLevel.ALL -> GameLevel.LEVEL1
        }
    }

    fun resetLevel(){
        currentLevel = GameLevel.LEVEL1
    }

    fun retrieveUserLastLevel() {
        val lastLevel =
            currentUser?.gameStat?.maxByOrNull { it.score }?.level ?: GameLevel.LEVEL1

        currentLevel = lastLevel
    }

    fun retrieveUserNextLevel() {
        val lastLevel =
            currentUser?.gameStat?.maxByOrNull { it.score }?.level ?: GameLevel.LEVEL1

        currentLevel = GameLevel.entries.getOrElse(lastLevel.ordinal + 1) { lastLevel }
    }

    fun updateScore() {
        currentUser?.let { user ->
            val previousGameLevelStat: GameLevelStat =
                user.gameStat.firstOrNull { it.level == currentLevel }?:
                GameLevelStat(currentLevel, -1L)
            Log.d("OM_TAG","GameViewModel: updateScore(): previousLevelScore = ${previousGameLevelStat.score}")
            val previousLevelScore = previousGameLevelStat.score
            val newLevelScore = when (previousLevelScore) {
                -1L -> timeElapsed
                else -> minOf(timeElapsed, previousLevelScore)
            }
            Log.d("OM_TAG","GameViewModel: updateScore(): newLevelScore = $newLevelScore")
            val newGameLevelStat =
                user.gameStat.firstOrNull{ it.level == currentLevel }?.copy(score = newLevelScore)?:
                GameLevelStat(currentLevel, newLevelScore)
            val newUserGameStat = user.gameStat.filterNot { it.level == currentLevel } + newGameLevelStat
            Log.d("OM_TAG","GameViewModel: updateScore(): newUserGameStat = $newUserGameStat")
            viewModelScope.launch { updateUser(user.copy(gameStat = newUserGameStat)) }
            Log.d("OM_TAG","GameViewModel: updateScore(): $pseudo 's $currentLevel score updated to $newLevelScore")
        }
    }

    init {
        retrieveUserNextLevel()
    }
}