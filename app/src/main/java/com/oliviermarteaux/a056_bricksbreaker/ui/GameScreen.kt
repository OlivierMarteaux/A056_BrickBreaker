package com.oliviermarteaux.a056_bricksbreaker.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.isActive

enum class GameState {
    STARTING, PLAYING, WIN, GAMEOVER
}

@Composable
fun GameScreen(navController: NavController, gameViewModel: GameViewModel) {
    var gameState by remember { mutableStateOf(GameState.STARTING) }
    
    val density = LocalDensity.current
    val paddleWidthDp = 120.dp
    val paddleHeightDp = 20.dp
    val ballRadiusDp = 10.dp
    val brickHeightDp = 40.dp
    
    val paddleWidthPx = with(density) { paddleWidthDp.toPx() }
    val paddleHeightPx = with(density) { paddleHeightDp.toPx() }
    val ballRadiusPx = with(density) { ballRadiusDp.toPx() }
    val brickHeightPx = with(density) { brickHeightDp.toPx() }
    
    var screenWidthPx by remember { mutableStateOf(0f) }
    var screenHeightPx by remember { mutableStateOf(0f) }
    
    var paddleX by remember { mutableStateOf(0f) }
    var ballPosition by remember { mutableStateOf(Offset.Zero) }
    var ballVelocity by remember { mutableStateOf(Offset.Zero) }
    var bricks by remember { mutableStateOf(setOf<Int>()) }
    var previousTime by remember { mutableStateOf(0L) }
    
    val speedLevel = gameViewModel.speed
    val baseSpeed = 200f // pixels per second for level 1
    val speedMultiplier = speedLevel
    
    fun initGame(w: Float, h: Float) {
        screenWidthPx = w
        screenHeightPx = h
        paddleX = (w - paddleWidthPx) / 2
        ballPosition = Offset(paddleX + paddleWidthPx / 2, h - paddleHeightPx - 50f - ballRadiusPx)
        ballVelocity = Offset(baseSpeed * speedMultiplier, -baseSpeed * speedMultiplier)
        bricks = (0..4).toSet()
        previousTime = 0L
    }
    
    LaunchedEffect(gameState) {
        if (gameState == GameState.PLAYING) {
            gameViewModel.startTime()
            while(isActive && gameState == GameState.PLAYING) {
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
                    if (newY - ballRadiusPx < brickHeightPx) {
                        val brickWidthPx = screenWidthPx / 5
                        val brickIndex = (newX / brickWidthPx).toInt().coerceIn(0, 4)
                        if (bricks.contains(brickIndex)) {
                            bricks = bricks - brickIndex
                            vy = -vy
                            newY = brickHeightPx + ballRadiusPx
                            
                            if (bricks.isEmpty()) {
                                gameState = GameState.WIN
                                gameViewModel.stopTime()
                                gameViewModel.updateScore()
                            }
                        }
                    }
                    
                    // Hit paddle
                    val paddleTop = screenHeightPx - paddleHeightPx - 50f
                    if (newY + ballRadiusPx >= paddleTop && newY - ballRadiusPx <= paddleTop + paddleHeightPx) {
                        if (newX >= paddleX && newX <= paddleX + paddleWidthPx) {
                            newY = paddleTop - ballRadiusPx
                            vy = -Math.abs(vy)
                        }
                    }
                    
                    // Bottom edge (GameOver)
                    if (newY + ballRadiusPx > screenHeightPx) {
                        gameState = GameState.GAMEOVER
                        gameViewModel.stopTime()
                        gameViewModel.updateScore()
                    }
                    
                    ballPosition = Offset(newX, newY)
                    ballVelocity = Offset(vx, vy)
                }
            }
        }
    }
    
    BoxWithConstraints(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        val wPx = constraints.maxWidth.toFloat()
        val hPx = constraints.maxHeight.toFloat()
        
        LaunchedEffect(wPx, hPx) {
            if (wPx > 0 && hPx > 0 && screenWidthPx == 0f) {
                initGame(wPx, hPx)
            }
        }
        
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    paddleX = (paddleX + dragAmount.x).coerceIn(0f, screenWidthPx - paddleWidthPx)
                }
            }
        ) {
            if (screenWidthPx == 0f) return@Canvas
            
            // Draw bricks
            val brickW = screenWidthPx / 5
            bricks.forEach { index ->
                drawRect(
                    color = Color.Blue,
                    topLeft = Offset(index * brickW, 0f),
                    size = Size(brickW - 4f, brickHeightPx - 4f)
                )
            }
            
            // Draw paddle
            val paddleTop = screenHeightPx - paddleHeightPx - 50f
            drawRect(
                color = Color.Green,
                topLeft = Offset(paddleX, paddleTop),
                size = Size(paddleWidthPx, paddleHeightPx)
            )
            
            // Draw ball
            drawCircle(
                color = Color.Red,
                radius = ballRadiusPx,
                center = ballPosition
            )
        }
        
        // Chrono overlay
        Text(
            text = "Time: ${gameViewModel.timeElapsed}s",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        )
        
        // Dialogs
        if (gameState == GameState.STARTING) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Ready to play?") },
                text = { Text("Click start to begin the game.") },
                confirmButton = {
                    Button(onClick = { gameState = GameState.PLAYING }) {
                        Text("Start")
                    }
                },
                dismissButton = {
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Go Back")
                    }
                }
            )
        }
        
        if (gameState == GameState.GAMEOVER) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Game Over") },
                text = { Text("You missed the ball! Time: ${gameViewModel.timeElapsed}s") },
                confirmButton = {
                    Button(onClick = { 
                        navController.popBackStack("home", false) 
                    }) {
                        Text("OK")
                    }
                }
            )
        }
        
        if (gameState == GameState.WIN) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("You Win!") },
                text = { Text("Congratulations! Time: ${gameViewModel.timeElapsed}s") },
                confirmButton = {
                    Button(onClick = { 
                        navController.popBackStack("home", false) 
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}