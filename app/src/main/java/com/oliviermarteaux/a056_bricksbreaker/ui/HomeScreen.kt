package com.oliviermarteaux.a056_bricksbreaker.ui

import android.R.attr.onClick
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.oliviermarteaux.shared.composables.SharedOutlinedTextField
import com.oliviermarteaux.shared.firebase.authentication.ui.AuthUserViewModel
import com.oliviermarteaux.shared.firebase.authentication.ui.UserAuthState

@Composable
fun HomeScreen(navController: NavController, gameViewModel: GameViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "App Icon",
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text("Bricks Breaker", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        
        Text("Speed Level")
        Slider(
            value = gameViewModel.speed,
            onValueChange = { gameViewModel.updateSpeed(it) },
            valueRange = 1f..10f,
            steps = 8
        )
        Text("Selected Speed: ${gameViewModel.speed.toInt()}", fontSize = 18.sp)
        
        Spacer(modifier = Modifier.height(48.dp))


//        gameViewModel.checkUserState(
//            onUserLogged = {
//                SharedOutlinedTextField(
//                    value = it.pseudo,
//                    onValueChange = { newPseudo ->
//                        gameViewModel.updateUser(it.copy(pseudo = newPseudo))
//                    },
//                    label = { Text("Pseudo") },
//                )
//            },
//            onNoUserLogged = {
//            }
//        )
        gameViewModel.user?.let{
            SharedOutlinedTextField(
                value = it.pseudo,
                onValueChange = { newPseudo ->
                    gameViewModel.updatePseudo(newPseudo)
                },
                label = "Pseudo",
                isError = it.pseudo.isBlank(),
                errorText = "Pseudo cannot be empty"
            )
        }

        
        Button(
            onClick = { navController.navigate("game") },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Start New Game")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { navController.navigate("score") },
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Scores")
        }
    }
}