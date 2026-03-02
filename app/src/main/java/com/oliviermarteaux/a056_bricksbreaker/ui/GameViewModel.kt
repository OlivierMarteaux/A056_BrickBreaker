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
    var speed by mutableFloatStateOf(5f)
        private set

    var timeElapsed by mutableLongStateOf(0L)
        private set

    var user: User? by mutableStateOf(null)
        private set

    var userUiState: UiState<User> by mutableStateOf(UiState.Loading)

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

    fun updatePseudo(pseudo: String){
        user = user?.copy(pseudo = pseudo)
    }

    fun updateScore() {
            //userRepository.updateScore(timeElapsed)
            checkUserState(
                onUserLogged = {
                    viewModelScope.launch {
                        userRepository.updateUser(it.copy(score = timeElapsed))
                    }
                },
                onNoUserLogged = {  }
            )
        }

    var userList: List<User> by mutableStateOf(emptyList())

    fun getAllScores() {
        viewModelScope.launch {
            userRepository.getAllUsers().collect { result ->
                result.fold(
                    onSuccess = { userList = it } ,
                    onFailure = {}
                )
            }
        }
    }

    private fun getCurrentUser(){
        viewModelScope.launch {
//            delay(1500) // for test
            userRepository.userAuthState
                .collect { currentUser ->
                    if (currentUser != null) {
                        userUiState = UiState.Success(currentUser)
                        user = currentUser
                        log.d("AccountViewModel: user updated to ${currentUser.email}")
                        log.d("AccountViewModel: userPhotoUrl = ${currentUser.photoUrl}")
                    } else {
                        userUiState = UiState.Error(Throwable("No user logged in"))
                        log.d("AccountViewModel: no user logged in")
                        log.d("AccountViewModel: userPhotoUrl = ${user?.photoUrl}")
                    }
                }
        }
    }

    init {
        userUiState = UiState.Loading
        getCurrentUser()
    }
}