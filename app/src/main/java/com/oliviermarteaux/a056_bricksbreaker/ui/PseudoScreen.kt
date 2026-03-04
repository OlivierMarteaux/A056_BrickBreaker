package com.oliviermarteaux.a056_bricksbreaker.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.oliviermarteaux.a056_bricksbreaker.R
import com.oliviermarteaux.shared.composables.ImageScaffold
import com.oliviermarteaux.shared.composables.SharedOutlinedTextField

@Composable
fun PseudoScreen(navController: NavController, gameViewModel: GameViewModel){

    ImageScaffold(
        image = painterResource( R.drawable.bricks_breaker_logo),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Please enter a pseudo")
            SharedOutlinedTextField(
                value = gameViewModel.pseudo,
                onValueChange = { gameViewModel.onPseudoChange(it) },
                label = "Pseudo"
            )
        }
    }

}