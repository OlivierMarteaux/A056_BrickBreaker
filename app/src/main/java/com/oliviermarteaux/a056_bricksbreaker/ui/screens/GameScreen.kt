package com.oliviermarteaux.a056_bricksbreaker.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.oliviermarteaux.a056_bricksbreaker.R
import com.oliviermarteaux.shared.compose.R as oR
import com.oliviermarteaux.a056_bricksbreaker.domain.Brick
import com.oliviermarteaux.a056_bricksbreaker.ui.GameUiState
import com.oliviermarteaux.a056_bricksbreaker.ui.GameViewModel
import com.oliviermarteaux.a056_bricksbreaker.ui.navigation.BricksBreakerScreen
import com.oliviermarteaux.shared.composables.IconSource
import com.oliviermarteaux.shared.composables.SharedIconButton
import com.oliviermarteaux.shared.composables.SharedScaffold
import kotlinx.coroutines.isActive

@Composable
fun GameScreen(navController: NavController, gameViewModel: GameViewModel) {
    var gameUiState by remember { mutableStateOf(GameUiState.STARTING) }

    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation

    val density = LocalDensity.current
    val paddleWidthDp = 120.dp
    val paddleHeightDp = if (orientation == Configuration.ORIENTATION_PORTRAIT) 50.dp else 10.dp
    val ballRadiusDp = 10.dp
    val brickHeightDp = if (orientation == Configuration.ORIENTATION_PORTRAIT) 40.dp else 20.dp
    
    val paddleWidthPx = with(density) { paddleWidthDp.toPx() }
    val paddleHeightPx = with(density) { paddleHeightDp.toPx() }
    val ballRadiusPx = with(density) { ballRadiusDp.toPx() }
    val brickHeightPx = with(density) { brickHeightDp.toPx() }
    
    var screenWidthPx by remember { mutableStateOf(0f) }
    var screenHeightPx by remember { mutableStateOf(0f) }

    val paddleTop = screenHeightPx - paddleHeightPx - 50f
    var paddleX by remember { mutableStateOf(0f) }
    var paddleY by remember { mutableStateOf(0f) }
    var ballPosition by remember { mutableStateOf(Offset.Zero) }
    var ballVelocity by remember { mutableStateOf(Offset.Zero) }
    var bricks by remember { mutableStateOf(setOf<Brick>()) }
    val brickCol: Int = 5
    val brickRow: Int = 4
    var previousTime by remember { mutableStateOf(0L) }
    
    val speedLevel = gameViewModel.speed
    val baseSpeed = 200f // pixels per second for level 1
    val speedMultiplier = speedLevel
    
    fun initGame(w: Float, h: Float) {
        screenWidthPx = w
        screenHeightPx = h
        paddleX = (w - paddleWidthPx) / 2
        paddleY = (h - paddleHeightPx)  - 100f
        ballPosition = Offset(paddleX + paddleWidthPx / 2, h - paddleHeightPx - 50f - ballRadiusPx)
        ballVelocity = Offset(baseSpeed * speedMultiplier, -baseSpeed * speedMultiplier)
        //bricks = (0..4).toSet()
        bricks = (0..<brickRow).flatMap { x ->
            (0..<brickCol).map { y ->
                Brick(x, y)
            }
        }.toSet()
        gameUiState = GameUiState.PLAYING
        previousTime = 0L
    }
    
    LaunchedEffect(gameUiState) {
        if (gameUiState == GameUiState.PLAYING) {
            gameViewModel.startTime()
            while(isActive && gameUiState == GameUiState.PLAYING) {
                withFrameNanos { time ->
                    if (previousTime == 0L) {
                        previousTime = time
                        return@withFrameNanos
                    }
                    val dt = (time - previousTime) / 1E9f
                    previousTime = time
                    
                    var newX = ballPosition.x + ballVelocity.x * dt
                    var newY = ballPosition.y + ballVelocity.y * dt
                    var vx = ballVelocity.x
                    var vy = ballVelocity.y
                    
                    // Bounce walls
                    if (newX - ballRadiusPx < 0) {
                        newX = ballRadiusPx
                        vx = -vx
                    } else if (newX + ballRadiusPx > screenWidthPx) {
                        newX = screenWidthPx - ballRadiusPx
                        vx = -vx
                    }
                    
                    if (newY - ballRadiusPx < 0) {
                        newY = ballRadiusPx
                        vy = -vy
                    }
                    
                    // Hit bricks
                    val brickW = screenWidthPx / brickCol

                    val hitBrick = bricks.firstOrNull { brick ->

                        val left = brick.col * brickW
                        val right = left + brickW
                        val top = brick.row * brickHeightPx + 200f
                        val bottom = top + brickHeightPx

                        newX + ballRadiusPx > left &&
                                newX - ballRadiusPx < right &&
                                newY + ballRadiusPx > top &&
                                newY - ballRadiusPx < bottom
                    }
                    hitBrick?.let { brick ->
                        bricks = bricks - brick

                        // simple bounce (vertical only for now)
                        vy = -vy

                        if (bricks.isEmpty()) {
                            gameUiState = GameUiState.WIN
                            gameViewModel.stopTime()
                            gameViewModel.updateScore()
                        }
                    }
                    
                    // Hit paddle
                    val paddleTop = screenHeightPx - paddleHeightPx - 50f
                    if (newY + ballRadiusPx >= paddleY && newY - ballRadiusPx <= paddleY + paddleHeightPx) {
                        if (newX >= paddleX && newX <= paddleX + paddleWidthPx) {
                            newY = paddleY - ballRadiusPx
                            vy = -Math.abs(vy)
                        }
                    }
                    
                    // Bottom edge (GameOver)
                    if (newY + ballRadiusPx > screenHeightPx) {
                        gameUiState = GameUiState.GAMEOVER
                        gameViewModel.stopTime()
                    }
                    
                    ballPosition = Offset(newX, newY)
                    ballVelocity = Offset(vx, vy)
                }
            }
        }
    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val wPx = constraints.maxWidth.toFloat()
        val hPx = constraints.maxHeight.toFloat()

        LaunchedEffect(wPx, hPx) {
            if (wPx > 0 && hPx > 0 && screenWidthPx == 0f) {
                initGame(wPx, hPx)
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            paddleX = (paddleX + dragAmount.x).coerceIn(0f, screenWidthPx - paddleWidthPx)
                            paddleY = (paddleY + dragAmount.y).coerceIn(0f, screenHeightPx - paddleHeightPx)
                        }
                    }
        ) {
            if (screenWidthPx == 0f) return@Canvas

            // Draw bricks
            val brickW = screenWidthPx / brickCol
            val brickLines = 4
            val cornerRadius = 18f
            val shadowOffset = 6f

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2027),
                        Color(0xFF203A43),
                        Color(0xFF2C5364)
                    )
                ),
                size = size
            )

            bricks.forEach { brick ->

                val left = brick.col * brickW
                val top = brick.row * brickHeightPx + 200f
                val width = brickW - 10f
                val height = brickHeightPx - 10f

                // Shadow
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.25f),
                    topLeft = Offset(left + shadowOffset, top + shadowOffset),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )

                // Main brick with gradient
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4FC3F7),  // light top
                            Color(0xFF1976D2)   // darker bottom
                        )
                    ),
                    topLeft = Offset(left, top),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )

                // Gloss highlight
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.15f),
                    topLeft = Offset(left, top),
                    size = Size(width, height / 2),
                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
                )
            }

            // Draw paddle
            val paddleCorner = 40f

            // Shadow
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.3f),
                topLeft = Offset(paddleX + 8f, paddleY + 8f),
                size = Size(paddleWidthPx, paddleHeightPx),
                cornerRadius = CornerRadius(paddleCorner, paddleCorner)
            )

            // Paddle body
            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF00E676),
                        Color(0xFF00C853)
                    )
                ),
                topLeft = Offset(paddleX, paddleY),
                size = Size(paddleWidthPx, paddleHeightPx),
                cornerRadius = CornerRadius(paddleCorner, paddleCorner)
            )

            // Glow
            drawCircle(
                color = Color.Red.copy(alpha = 0.25f),
                radius = ballRadiusPx * 1.8f,
                center = ballPosition
            )

            // Main ball
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFFF5252),
                        Color(0xFFD50000)
                    ),
                    center = ballPosition,
                    radius = ballRadiusPx
                ),
                radius = ballRadiusPx,
                center = ballPosition
            )
        }

        // Chrono overlay
        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 50.dp)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.time_s, gameViewModel.timeElapsed),
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp)
            )
        }

        SharedIconButton(
            icon =  IconSource.VectorIcon(Icons.AutoMirrored.Filled.ArrowBack),
            onClick = {navController.popBackStack()},
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 50.dp)
                .padding(16.dp)
        )

        // Dialogs
        if (gameUiState == GameUiState.STARTING) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(stringResource(R.string.ready_to_play)) },
                text = { Text(stringResource(R.string.click_start_to_begin_the_game)) },
                confirmButton = {
                    Button(onClick = { gameUiState = GameUiState.PLAYING }) {
                        Text(stringResource(R.string.start))
                    }
                },
                dismissButton = {
                    Button(onClick = { navController.popBackStack() }) {
                        Text(stringResource(R.string.go_back))
                    }
                }
            )
        }

        if (gameUiState == GameUiState.GAMEOVER) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(stringResource(R.string.game_over)) },
                text = { Text(
                    stringResource(
                        R.string.you_missed_the_ball_time_s,
                        gameViewModel.timeElapsed
                    )) },
                confirmButton = {
                    Button(onClick = {
                        navController.popBackStack(BricksBreakerScreen.Home.route, false)
                    }) {
                        Text(stringResource(oR.string.ok))
                    }
                }
            )
        }

        if (gameUiState == GameUiState.WIN) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text(stringResource(R.string.you_win)) },
                text = { Text(
                    stringResource(
                        R.string.congratulations_time_s,
                        gameViewModel.timeElapsed
                    )) },
                confirmButton = {
                    Button(onClick = {
                        navController.popBackStack(BricksBreakerScreen.Home.route, false)
                    }) {
                        Text(stringResource(oR.string.ok))
                    }
                }
            )
        }
    }
}