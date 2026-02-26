package com.oliviermarteaux.a056_bricksbreaker.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.oliviermarteaux.a056_bricksbreaker.data.UserRepository
import com.oliviermarteaux.a056_bricksbreaker.domain.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameViewModel(private val userRepository: UserRepository) : ViewModel() {
    var speed by mutableStateOf(5f)
        private set
        
    var timeElapsed by mutableStateOf(0L)
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
        userRepository.updateScore(timeElapsed)
    }
    
    fun getAllScores(): List<User> {
        return userRepository.getAllScores()
    }
}

class GameViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}