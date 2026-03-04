package com.oliviermarteaux.a056_bricksbreaker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.oliviermarteaux.shared.composables.ImageScaffold
import com.oliviermarteaux.shared.composables.SharedOutlinedTextField
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.composables.spacer.SpacerMedium
import com.oliviermarteaux.shared.composables.spacer.SpacerXl
import com.oliviermarteaux.shared.composables.spacer.SpacerXs

@Composable
fun HomeScreen(navController: NavController, gameViewModel: GameViewModel) {

    SharedScaffold(
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
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (gameViewModel.currentUser?.pseudo?.isEmpty()?:true) {
                    Text("Please enter a pseudo")
                    SpacerXs()
                    SharedOutlinedTextField(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        value = gameViewModel.pseudo,
                        onValueChange = { gameViewModel.onPseudoChange(it) },
                        label = "Pseudo",
                        isError = gameViewModel.pseudo.isEmpty(),
                        errorText = "Pseudo cannot be empty"
                    )
                } else {
                    Text("Welcome, ${gameViewModel.currentUser?.pseudo?: ""}!")
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
                    onClick = {
                        gameViewModel.updatePseudo()
                        navController.navigate("game")
                              },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Start New Game")
                }

                SpacerMedium()

                Button(
                    onClick = {
                        gameViewModel.updatePseudo()
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