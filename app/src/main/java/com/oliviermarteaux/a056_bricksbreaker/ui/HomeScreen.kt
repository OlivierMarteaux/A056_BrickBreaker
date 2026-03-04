package com.oliviermarteaux.a056_bricksbreaker.ui

import android.R.attr.onClick
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.oliviermarteaux.a056_bricksbreaker.R
import com.oliviermarteaux.a056_bricksbreaker.ui.navigation.BricksBreakerScreen
import com.oliviermarteaux.a056_bricksbreaker.ui.navigation.RootNavGraph
import com.oliviermarteaux.shared.composables.ImageScaffold
import com.oliviermarteaux.shared.composables.SharedOutlinedTextField
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.spacer.SpacerMedium
import com.oliviermarteaux.shared.composables.spacer.SpacerXl
import com.oliviermarteaux.shared.composables.spacer.SpacerXs
import com.oliviermarteaux.shared.firebase.authentication.ui.UserAuthState
import com.oliviermarteaux.shared.navigation.Screen

@Composable
fun HomeScreen(navController: NavController, gameViewModel: GameViewModel) {

    SharedScaffold(
        title = "Bricks Breaker",
        onBackClick = { navController.navigate(Screen.Splash.route) }
    ) { innerPadding ->
        ImageScaffold(
            image = painterResource(R.drawable.bricks_breaker_logo),
            innerPadding = innerPadding,
            landscapeHorizontalPadding= 85.dp,
            landscapeCentralPadding = 85.dp,
            formPortraitHorizontalPadding = 24.dp,
            imageModifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier.height(120.dp)
                ) {
                    when (gameViewModel.userAuthState) {

                        is UserAuthState.Connected -> {
                            if (gameViewModel.currentUser?.pseudo?.isBlank() ?: true) {
                                Text("Please enter a pseudo")
                                SpacerXs()
                                SharedOutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(0.8f),
                                    value = gameViewModel.pseudo,
                                    onValueChange = { gameViewModel.onPseudoChange(it) },
                                    label = "Pseudo",
                                    isError = gameViewModel.pseudo.isBlank(),
                                    errorText = "Pseudo cannot be blank or empty"
                                )
                            } else {
                                Text("Welcome, ${gameViewModel.currentUser?.pseudo ?: ""}!")
                            }
                        }

                        else -> {

                        }
                    }
                }


                SpacerXl()

                Text("Speed Level")
                SpacerXs()
                Slider(
                    value = gameViewModel.speed,
                    onValueChange = { gameViewModel.updateSpeed(it) },
                    valueRange = 1f..10f,
                    steps = 8,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                SpacerXs()
                Text("Selected Speed: ${gameViewModel.speed.toInt()}", fontSize = 18.sp)

                SpacerXl()

                Button(
                    enabled =
                        if (gameViewModel.currentUser?.pseudo?.isBlank() ?: true) gameViewModel.pseudo.isNotBlank()
                        else true
                    ,
                    onClick = {
                        if (gameViewModel.currentUser?.pseudo?.isBlank()?:true) gameViewModel.updatePseudo()
                        navController.navigate("game")
                              },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Start New Game")
                }

                SpacerMedium()

                Button(
                    enabled =
                        if (gameViewModel.currentUser?.pseudo?.isBlank() ?: true) gameViewModel.pseudo.isNotBlank()
                        else true,
                    onClick = {
                        if (gameViewModel.currentUser?.pseudo?.isBlank()?:true) gameViewModel.updatePseudo()
                        gameViewModel.getAllUsers()
                        navController.navigate("score")
                              },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Scores")
                }
            }
        }
    }
}