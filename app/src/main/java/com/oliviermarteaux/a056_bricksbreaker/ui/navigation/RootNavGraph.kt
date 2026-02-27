package com.oliviermarteaux.a056_bricksbreaker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.oliviermarteaux.a056_bricksbreaker.R
import com.oliviermarteaux.a056_bricksbreaker.ui.GameViewModel
import com.oliviermarteaux.a056_bricksbreaker.ui.HomeScreen
import com.oliviermarteaux.a056_bricksbreaker.ui.GameScreen
import com.oliviermarteaux.a056_bricksbreaker.ui.ScoreScreen
import com.oliviermarteaux.shared.navigation.SharedNavGraph
import com.oliviermarteaux.shared.navigation.authNavGraph
import com.oliviermarteaux.shared.ui.theme.SharedShapes

@Composable
fun RootNavGraph(
    navHostController: NavHostController,
    logoRes: Int = -1,
    startDestination: String = SharedNavGraph.AUTH
) {
    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        //_ shared authentication SharedNavGraph
        authNavGraph(
            navHostController = navHostController,
            logoRes = logoRes,
            imageModifier = Modifier.clip(shape = SharedShapes.medium),
            serverClientIdStringRes = R.string.default_web_client_id,
            navigateToHomeScreen = {
                navHostController.navigate(SharedNavGraph.APP) {
                    popUpTo(SharedNavGraph.AUTH) { inclusive = true }
                }
            }
        )

        navigation(
            startDestination = BricksBreakerScreen.Home.route,
            route = SharedNavGraph.APP
        ) {
            /*_ HOME SCREEN ##############################################################################*/
            composable(route = BricksBreakerScreen.Home.route) { backStackEntry ->

                val parentEntry = remember(backStackEntry) {
                    navHostController.getBackStackEntry(SharedNavGraph.APP)
                }

                val gameViewModel: GameViewModel = hiltViewModel(parentEntry)

                HomeScreen(
                    gameViewModel = gameViewModel,
                    navController = navHostController
                )
            }
            /*_ GAME SCREEN ##############################################################################*/
            composable(route = BricksBreakerScreen.Game.route) {backStackEntry ->

                val parentEntry = remember(backStackEntry) {
                    navHostController.getBackStackEntry(SharedNavGraph.APP)
                }

                val gameViewModel: GameViewModel = hiltViewModel(parentEntry)

                GameScreen(
                    gameViewModel = gameViewModel,
                    navController = navHostController,
                )
            }
            /*_ SCORE SCREEN ##############################################################################*/
            composable(route = BricksBreakerScreen.Score.route) { backStackEntry ->

                val parentEntry = remember(backStackEntry) {
                    navHostController.getBackStackEntry(SharedNavGraph.APP)
                }

                val gameViewModel: GameViewModel = hiltViewModel(parentEntry)

                ScoreScreen(
                    gameViewModel = gameViewModel,
                    navController = navHostController,
                )
            }
        }
    }
}