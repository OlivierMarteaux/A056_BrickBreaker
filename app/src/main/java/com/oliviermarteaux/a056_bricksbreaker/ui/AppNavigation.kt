package com.oliviermarteaux.a056_bricksbreaker.ui

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.oliviermarteaux.a056_bricksbreaker.data.UserRepository

@Composable
fun AppNavigation(
    userRepository: UserRepository = UserRepository(),
    gameViewModel: GameViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController, userRepository = userRepository)
        }
        composable("home") {
            HomeScreen(navController = navController, gameViewModel = gameViewModel)
        }
        composable("game") {
            GameScreen(navController = navController, gameViewModel = gameViewModel)
        }
        composable("scores") {
            ScoreScreen(navController = navController, gameViewModel = gameViewModel)
        }
    }
}