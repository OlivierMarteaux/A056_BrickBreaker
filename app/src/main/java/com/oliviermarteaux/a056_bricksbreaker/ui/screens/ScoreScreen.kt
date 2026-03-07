package com.oliviermarteaux.a056_bricksbreaker.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.oliviermarteaux.a056_bricksbreaker.R
import com.oliviermarteaux.shared.compose.R as oR
import com.oliviermarteaux.a056_bricksbreaker.ui.GameViewModel
import com.oliviermarteaux.shared.composables.SharedScaffold
import com.oliviermarteaux.shared.firebase.authentication.domain.model.GameLevel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.oliviermarteaux.shared.firebase.authentication.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreen(
    navController: NavController,
    gameViewModel: GameViewModel,
    containerColor: Color = MaterialTheme.colorScheme.background,
    topAppBarColors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
) {

    BackHandler() {
        gameViewModel.retrieveUserNextLevel()
        navController.popBackStack()
    }

    var selectedLevel: GameLevel by remember { mutableStateOf(gameViewModel.currentLevel) }
    lateinit var noScoreList: List<User>
    lateinit var scoreList: List<User>
    lateinit var userList: List<User>
    var score: Long
    val lastLevel = GameLevel.LEVEL10

    when (selectedLevel) {
        GameLevel.ALL -> {
            noScoreList = gameViewModel.userList.filter {
                it.getCompletedGameScore(lastLevel) < 0
            }
            scoreList = gameViewModel.userList.filter {
                it.getCompletedGameScore(lastLevel) >= 0
            }
            userList = scoreList.sortedBy {
                it.getCompletedGameScore(lastLevel)
            } + noScoreList.sortedBy { it.pseudo }
        }
        else -> {
            noScoreList = gameViewModel.userList.filter { it.scoreForLevel(selectedLevel) < 0 }
            scoreList = gameViewModel.userList.filter { it.scoreForLevel(selectedLevel) >= 0 }
            userList =
                scoreList.sortedBy { it.scoreForLevel(selectedLevel) } + noScoreList.sortedBy { it.pseudo }
        }
    }
    
    SharedScaffold(
        containerColor = containerColor,
        topAppBarColors = topAppBarColors,
        title = stringResource(selectedLevel.labelRes),
        onBackClick = {
            gameViewModel.retrieveUserNextLevel()
            navController.popBackStack()
                      },
        onMenuItem1Click = { selectedLevel = GameLevel.ALL },
        onMenuItem2Click = { selectedLevel = GameLevel.LEVEL1 },
        onMenuItem3Click = { selectedLevel = GameLevel.LEVEL2 },
        onMenuItem4Click = { selectedLevel = GameLevel.LEVEL3 },
        onMenuItem5Click = { selectedLevel = GameLevel.LEVEL4 },
        onMenuItem6Click = { selectedLevel = GameLevel.LEVEL5 },
        onMenuItem7Click = { selectedLevel = GameLevel.LEVEL6 },
        onMenuItem8Click = { selectedLevel = GameLevel.LEVEL7 },
        onMenuItem9Click = { selectedLevel = GameLevel.LEVEL8 },
        onMenuItem10Click = { selectedLevel = GameLevel.LEVEL9 },
        onMenuItem11Click = { selectedLevel = GameLevel.LEVEL10 },
        menuItem1Title = stringResource(oR.string.all_levels),
        menuItem2Title = stringResource(oR.string.level_1),
        menuItem3Title = stringResource(oR.string.level_2),
        menuItem4Title = stringResource(oR.string.level_3),
        menuItem5Title = stringResource(oR.string.level_4),
        menuItem6Title = stringResource(oR.string.level_5),
        menuItem7Title = stringResource(oR.string.level_6),
        menuItem8Title = stringResource(oR.string.level_7),
        menuItem9Title = stringResource(oR.string.level_8),
        menuItem10Title = stringResource(oR.string.level_9),
        menuItem11Title = stringResource(oR.string.level_10),
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(userList.filter { !it.pseudo.isBlank() }) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = user.pseudo,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            score = if (selectedLevel == GameLevel.ALL) {
                                user.getCompletedGameScore(lastLevel)
                            } else {
                                user.scoreForLevel(selectedLevel)
                            }
                            Text(
                                text = if (score < 0) stringResource(R.string.score_line_s) else "$score s",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}