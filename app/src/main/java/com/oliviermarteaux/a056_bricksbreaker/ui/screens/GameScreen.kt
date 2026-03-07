package com.oliviermarteaux.a056_bricksbreaker.ui.screens

import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.oliviermarteaux.a056_bricksbreaker.R
import com.oliviermarteaux.a056_bricksbreaker.domain.Brick
import com.oliviermarteaux.a056_bricksbreaker.domain.Bumper
import com.oliviermarteaux.a056_bricksbreaker.domain.Wall
import com.oliviermarteaux.a056_bricksbreaker.ui.GameUiState
import com.oliviermarteaux.a056_bricksbreaker.ui.GameViewModel
import com.oliviermarteaux.a056_bricksbreaker.ui.navigation.BricksBreakerScreen
import com.oliviermarteaux.shared.composables.IconSource
import com.oliviermarteaux.shared.composables.SharedIconButton
import com.oliviermarteaux.shared.firebase.authentication.domain.model.GameLevel
import com.oliviermarteaux.shared.navigation.Screen
import kotlinx.coroutines.isActive
import kotlin.math.sqrt
import com.oliviermarteaux.shared.compose.R as oR


@Composable
fun GameScreen(navController: NavController, gameViewModel: GameViewModel) {

    var gameUiState by remember { mutableStateOf(GameUiState.STARTING) }

    BackHandler() {
        gameUiState = GameUiState.PAUSE
        gameViewModel.retrieveUserNextLevel()
        navController.popBackStack()
    }

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

    //_ walls def for next levels
    val topDownWalls = when(gameViewModel.currentLevel) {
        GameLevel.LEVEL2  -> setOf(Wall(Offset(screenWidthPx/2f, screenHeightPx/2f ), Size(400f, 100f)))
        GameLevel.LEVEL4  -> setOf(
            Wall(Offset(screenWidthPx/4f, screenHeightPx/3f ), Size(400f, 100f)),
            Wall(Offset(3*screenWidthPx/5f, 2* screenHeightPx/5f), Size(400f, 100f))
        )
        GameLevel.LEVEL10  -> setOf(
            Wall(Offset(screenWidthPx/2f-200f, 2*screenHeightPx/6f ), Size(400f, 200f)),
            Wall(Offset(screenWidthPx/2f-150f, 3*screenHeightPx/6f ), Size(300f, 200f)),
            Wall(Offset(screenWidthPx/2f-50f, 4*screenHeightPx/6f ), Size(100f, 200f)),
            )
        else -> emptySet()
    }
    val bumpers = when(gameViewModel.currentLevel) {
        GameLevel.LEVEL1 -> setOf(Bumper(200f, Offset(screenWidthPx/2f, screenHeightPx/2f )))
        GameLevel.LEVEL5 -> setOf(
            Bumper(200f, Offset(screenWidthPx/4f, screenHeightPx/3f )),
            Bumper(200f, Offset(3*screenWidthPx/4f, screenHeightPx/3f )),
        )
        GameLevel.LEVEL6 -> setOf(
            Bumper(100f, Offset(screenWidthPx/4f, screenHeightPx/3f )),
            Bumper(100f, Offset(3*screenWidthPx/5f, 2*screenHeightPx/4f )),
            Bumper(100f, Offset(3*screenWidthPx/4f, 2*screenHeightPx/3f )),
        )
        GameLevel.LEVEL7 -> setOf(
                Bumper(100f, Offset(screenWidthPx/7f-50, 2*screenHeightPx/7f )),
                Bumper(100f, Offset(screenWidthPx/7f-50, 3*screenHeightPx/7f )),
                Bumper(100f, Offset(screenWidthPx/7f-50, 4*screenHeightPx/7f )),
                Bumper(100f, Offset(7*screenWidthPx/7f-100, 2*screenHeightPx/7f )),
                Bumper(100f, Offset(7*screenWidthPx/7f-100, 3*screenHeightPx/7f )),
                Bumper(100f, Offset(7*screenWidthPx/7f-100, 4*screenHeightPx/7f )),
            )
        GameLevel.LEVEL8 -> setOf(
            Bumper(100f, Offset(1*screenWidthPx/7f+50, 2*screenHeightPx/4f )),
            Bumper(100f, Offset(4*screenWidthPx/7f-100, 2*screenHeightPx/4f )),
            Bumper(100f, Offset(7*screenWidthPx/7f-250, 2*screenHeightPx/4f )),
        )
        GameLevel.LEVEL9 -> setOf(
            Bumper(50f, Offset(5*screenWidthPx/10f, 1000f )),
            Bumper(50f, Offset(4*screenWidthPx/10f, 1200f )),
            Bumper(50f, Offset(6*screenWidthPx/10f, 1200f )),
            Bumper(50f, Offset(3*screenWidthPx/10f, 1400f )),
            Bumper(50f, Offset(7*screenWidthPx/10f, 1400f )),
            Bumper(50f, Offset(3*screenWidthPx/10f, 1600f )),
            Bumper(50f, Offset(7*screenWidthPx/10f, 1600f )),
            Bumper(50f, Offset(4*screenWidthPx/10f, 1800f )),
            Bumper(50f, Offset(6*screenWidthPx/10f, 1800f )),
            Bumper(50f, Offset(5*screenWidthPx/10f, 2000f )),
        )
        else -> emptySet()

    }

    val speedLevel = gameViewModel.speed
    val baseSpeed = 400f // pixels per second for level 1
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
//        gameUiState = GameUiState.PLAYING
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

                    //_ Hit top-down walls
                    val hitTopDownWall = topDownWalls.forEach{
                        if (newY - ballRadiusPx <= it.topLeft.y + it.size.height && newY + ballRadiusPx >= it.topLeft.y + it.size.height) {
                            if (newX >= it.topLeft.x && newX <= it.topLeft.x + it.size.width){
                            vy = Math.abs(vy)
                                }
                        }
                    }

                    //_ Hit bumpers
                    bumpers.forEach { bumper ->

                        val dx = newX - bumper.center.x
                        val dy = newY - bumper.center.y
                        val dist = sqrt(dx * dx + dy * dy)

                        if (dist < bumper.radius + ballRadiusPx) {

                            val nx = dx / dist
                            val ny = dy / dist

                            val dot = vx * nx + vy * ny

                            vx -= 2 * dot * nx
                            vy -= 2 * dot * ny

                            val overlap = bumper.radius + ballRadiusPx - dist
                            newX += nx * overlap
                            newY += ny * overlap
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

//        val bumperShader = ImageShader(
//            image = ImageBitmap.imageResource(R.drawable.bumper),
//            tileModeX = TileMode.Clamp,
//            tileModeY = TileMode.Clamp
//        )
//        val paddleShader = ImageShader(
//            image = ImageBitmap.imageResource(R.drawable.bumper),
//            tileModeX = TileMode.Clamp,
//            tileModeY = TileMode.Clamp
//        )

        val bumperImage = ImageBitmap.imageResource(R.drawable.bumper)
        val brickImage = ImageBitmap.imageResource(R.drawable.brick)
        val paddleImage = ImageBitmap.imageResource(R.drawable.paddle)

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        paddleX =
                            (paddleX + dragAmount.x).coerceIn(0f, screenWidthPx - paddleWidthPx)
                        paddleY =
                            (paddleY + dragAmount.y).coerceIn(/*0f*/screenHeightPx - 5 * paddleHeightPx,
                                screenHeightPx - paddleHeightPx
                            )
                    }
                }
        ) {
            if (screenWidthPx == 0f) return@Canvas

            // Draw bricks
            val brickW = screenWidthPx / brickCol
            val brickLines = 4
            val cornerRadius = 18f
            val shadowOffset = 6f

            // Draw background gradient
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

            //_ Draw top-down wall
            topDownWalls.forEach {
                drawRect(
                    topLeft = it.topLeft,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x00F44336),
                            Color(0xFFF44336),
                        ),
                        startY = it.topLeft.y,
                        endY = it.topLeft.y + it.size.height
                    ),
                    size = it.size,
                )
            }

            //_ Draw bumper

//            bumpers.forEach {
//                drawCircle(
//                    brush = ShaderBrush(bumperShader),
//                    radius = it.radius,
//                    center = it.center
//                )
//            }
            bumpers.forEach {

                drawImage(
                    image = bumperImage,
                    dstOffset = IntOffset(
                        (it.center.x - it.radius).toInt(),
                        (it.center.y - it.radius).toInt()
                    ),
                    dstSize = IntSize(
                        (it.radius * 2).toInt(),
                        (it.radius * 2).toInt()
                    )
                )
            }

            // Draw paddle area limit line

            drawLine(
                color = Color.Red,
                strokeWidth = 2f,
                start = Offset(0f, screenHeightPx - 5 * paddleHeightPx),
                end = Offset(screenWidthPx, screenHeightPx - 5 * paddleHeightPx)
            )

            bricks.forEach { brick ->

                val left = brick.col * brickW
                val top = brick.row * brickHeightPx + 200f
                val width = brickW
                val height = brickHeightPx

                drawImage(
                    image = brickImage,
                    dstOffset = IntOffset(
                        left.toInt(),
                        top.toInt()
                    ),
                    dstSize = IntSize(
                        width.toInt(),
                        height.toInt()
                    )
                )

//                // Shadow
//                drawRoundRect(
//                    color = Color.Black.copy(alpha = 0.25f),
//                    topLeft = Offset(left + shadowOffset, top + shadowOffset),
//                    size = Size(width, height),
//                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
//                )
//
//                // Main brick with gradient
//                drawRoundRect(
//                    brush = Brush.verticalGradient(
//                        colors = listOf(
//                            Color(0xFF4FC3F7),  // light top
//                            Color(0xFF1976D2)   // darker bottom
//                        )
//                    ),
//                    topLeft = Offset(left, top),
//                    size = Size(width, height),
//                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
//                )
//
//                // Gloss highlight
//                drawRoundRect(
//                    color = Color.White.copy(alpha = 0.15f),
//                    topLeft = Offset(left, top),
//                    size = Size(width, height / 2),
//                    cornerRadius = CornerRadius(cornerRadius, cornerRadius)
//                )
            }

            // Draw paddle
            drawImage(
                image = paddleImage,
                dstOffset = IntOffset(
                    (paddleX).toInt(),
                    (paddleY).toInt()
                ),
                dstSize = IntSize(
                    (paddleWidthPx).toInt(),
                    (paddleHeightPx).toInt()
                )
            )

//            val paddleCorner = 40f
////
////            // Shadow
//            drawRoundRect(
//                color = Color.Black.copy(alpha = 0.3f),
//                topLeft = Offset(paddleX + 8f, paddleY + 8f),
//                size = Size(paddleWidthPx, paddleHeightPx),
//                cornerRadius = CornerRadius(paddleCorner, paddleCorner)
//            )
////
////            // Paddle body
//            drawRoundRect(
//                brush = Brush.horizontalGradient(
//                    colors = listOf(
//                        Color(0xFF00E676),
//                        Color(0xFF00C853)
//                    )
//                ),
////                brush = ShaderBrush(paddleShader),
//                topLeft = Offset(paddleX, paddleY),
//                size = Size(paddleWidthPx, paddleHeightPx),
//                cornerRadius = CornerRadius(paddleCorner, paddleCorner)
//            )
////
////            // Glow
//            drawCircle(
//                color = Color.Red.copy(alpha = 0.25f),
//                radius = ballRadiusPx * 1.8f,
//                center = ballPosition
//            )

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

        // Back button
        SharedIconButton(
            icon =  IconSource.VectorIcon(Icons.AutoMirrored.Filled.ArrowBack),
            tint = Color.Black,
            onClick = {
                gameUiState = GameUiState.PAUSE
                gameViewModel.retrieveUserNextLevel()
                navController.popBackStack()
                      },
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
                title = { Text(stringResource( gameViewModel.currentLevel.labelRes) +". " + stringResource(R.string.ready_to_play)) },
                text = { Text(stringResource(R.string.click_start_to_begin_the_game)) },
                confirmButton = {
                    Button(onClick = { gameUiState = GameUiState.PLAYING }) {
                        Text(stringResource(R.string.start))
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        gameUiState = GameUiState.PAUSE
                        gameViewModel.retrieveUserNextLevel()
                        navController.popBackStack()
                    }) {
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
                        gameUiState = GameUiState.PAUSE
                        gameViewModel.retrieveUserNextLevel()
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
                        initGame(wPx, hPx)
                        gameViewModel.setNextLevel()
                        gameUiState = GameUiState.STARTING
                    }){
                        Text(stringResource(oR.string.ok))
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        gameViewModel.retrieveUserNextLevel()
                        navController.popBackStack(BricksBreakerScreen.Home.route, false)
                    }) {
                        Text("Back to home")
                    }
                }
            )
        }
    }
}