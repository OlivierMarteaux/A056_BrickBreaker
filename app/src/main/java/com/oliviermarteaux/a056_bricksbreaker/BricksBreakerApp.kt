package com.oliviermarteaux.a056_bricksbreaker

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.oliviermarteaux.a056_bricksbreaker.ui.navigation.BricksBreakerScreen
import com.oliviermarteaux.a056_bricksbreaker.ui.navigation.RootNavGraph
import com.oliviermarteaux.shared.composables.startup.DismissKeyboardOnTapOutside
import com.oliviermarteaux.shared.navigation.LogRoutes
import com.oliviermarteaux.shared.navigation.Screen
import com.oliviermarteaux.shared.navigation.SharedNavGraph
import com.oliviermarteaux.shared.utils.TestConfig

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun BricksBreakerApp(){

    val navController = rememberNavController()

    Log.d("OM_TAG", "BuildConfig: Debug = ${BuildConfig.DEBUG}")

    val startDestination: String =
        if (TestConfig.isTest) {
        Log.d("OM_TAG", "start screen = ${BricksBreakerScreen.Home.route}")
            SharedNavGraph.APP
        } else {
            Log.d("OM_TAG", "start screen = ${Screen.Splash.route}")
            SharedNavGraph.AUTH
        }

//    Surface {
        DismissKeyboardOnTapOutside {
            RootNavGraph(
                navHostController = navController,
                startDestination = startDestination,
                logoRes = R.drawable.bricks_breaker_logo,
                containerColor = MaterialTheme.colorScheme.background,
                topAppBarColors = TopAppBarDefaults.topAppBarColors()
            )
        }
//    }

    LogRoutes(navController)
}