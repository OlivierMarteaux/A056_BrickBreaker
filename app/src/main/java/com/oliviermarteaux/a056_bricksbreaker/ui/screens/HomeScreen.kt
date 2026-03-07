package com.oliviermarteaux.a056_bricksbreaker.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.oliviermarteaux.a056_bricksbreaker.R
import com.oliviermarteaux.a056_bricksbreaker.ui.GameViewModel
import com.oliviermarteaux.a056_bricksbreaker.ui.navigation.BricksBreakerScreen
import com.oliviermarteaux.shared.composables.ImageScaffold
import com.oliviermarteaux.shared.composables.SharedOutlinedTextField
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.extensions.fontScaledHeight
import com.oliviermarteaux.shared.composables.spacer.SpacerMedium
import com.oliviermarteaux.shared.composables.spacer.SpacerXl
import com.oliviermarteaux.shared.composables.spacer.SpacerXs
import com.oliviermarteaux.shared.composables.texts.TextTitleLarge
import com.oliviermarteaux.shared.firebase.authentication.domain.model.GameLevel
import com.oliviermarteaux.shared.firebase.authentication.ui.UserAuthState
import com.oliviermarteaux.shared.navigation.Screen
import com.oliviermarteaux.shared.ui.UiState
import kotlin.math.roundToInt

@Composable
fun HomeScreen(navController: NavController, gameViewModel: GameViewModel) {

    SharedScaffold(
        title = stringResource(R.string.bricks_breaker),
        onBackClick = { navController.navigate(Screen.Splash.route){popUpTo(0) { inclusive = true } } }
    ) { innerPadding ->

        BackHandler() { navController.navigate(Screen.Splash.route){popUpTo(0) { inclusive = true } } }
        ImageScaffold(
            image = painterResource(R.drawable.bricks_breaker_logo),
            innerPadding = innerPadding,
            landscapeHorizontalPadding= 85.dp,
            landscapeCentralPadding = 85.dp,
            formPortraitHorizontalPadding = 24.dp,
            imageModifier = Modifier.fillMaxWidth()
        ) {
            when (gameViewModel.currentUserUiState){
                is UiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){CircularProgressIndicator()}
                }
                is UiState.Success -> {
                    HomeScreenBody(
                        navController = navController,
                        gameViewModel = gameViewModel
                    )
                }
                else -> {
                }
            }
        }
    }
}

@Composable
fun HomeScreenBody(
    navController: NavController,
    gameViewModel: GameViewModel
){
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.fontScaledHeight(
                fontSize = 12.sp,
                scale = 10f,
                min = 110.dp,
                max = 170.dp
            )
        ) {
            when (gameViewModel.userAuthState) {

                is UserAuthState.Connected -> {
                    if (gameViewModel.currentUser?.pseudo?.isBlank() ?: true) {
                        Text(stringResource(R.string.please_enter_a_pseudo))
                        SpacerXs()
                        SharedOutlinedTextField(
                            modifier = Modifier.fillMaxWidth(0.8f),
                            value = gameViewModel.pseudo,
                            onValueChange = { gameViewModel.onPseudoChange(it) },
                            label = stringResource(R.string.pseudo),
                            isError = gameViewModel.pseudo.isBlank(),
                            errorText = "Pseudo cannot be blank or empty"
                        )
                    } else {
                        SpacerXl()
                        TextTitleLarge(
                            stringResource(
                                R.string.welcome_player,
                                gameViewModel.currentUser?.pseudo ?: ""
                            ))
                    }
                }
                else -> {}
            }
        }


        SpacerXl()

        Text(stringResource(R.string.speed_level))
        SpacerXs()
        Slider(
            value = gameViewModel.speed,
            onValueChange = { gameViewModel.updateSpeed(it) },
            valueRange = 1f..10f,
            steps = 8,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        SpacerXs()
        Text(stringResource(R.string.selected_speed, gameViewModel.speed.roundToInt()), fontSize = 18.sp)

        SpacerXl()

        Button(
            enabled =
                if (gameViewModel.currentUser?.pseudo?.isBlank() ?: true) gameViewModel.pseudo.isNotBlank()
                else true
            ,
            onClick = {
                if (gameViewModel.currentUser?.pseudo?.isBlank()?:true) gameViewModel.updatePseudo()
                gameViewModel.resetLevel()
                navController.navigate(BricksBreakerScreen.Game.route)
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(stringResource(R.string.start_new_game))
        }

        SpacerMedium()

        Button(
            enabled =
                if (gameViewModel.currentUser?.pseudo?.isBlank() ?: true) gameViewModel.pseudo.isNotBlank()
                else true
            ,
            onClick = {
                if (gameViewModel.currentUser?.pseudo?.isBlank()?:true) gameViewModel.updatePseudo()
                navController.navigate(BricksBreakerScreen.Game.route)
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(stringResource(R.string.continue_game))
        }

        SpacerMedium()

        Button(
            enabled =
                if (gameViewModel.currentUser?.pseudo?.isBlank() ?: true) gameViewModel.pseudo.isNotBlank()
                else true,
            onClick = {
                if (gameViewModel.currentUser?.pseudo?.isBlank()?:true) gameViewModel.updatePseudo()
                gameViewModel.getAllUsers()
                navController.navigate(BricksBreakerScreen.Score.route)
                gameViewModel.retrieveUserLastLevel()
            },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(stringResource(R.string.scores))
        }
        SpacerXl()
    }
}