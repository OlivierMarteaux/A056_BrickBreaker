package com.oliviermarteaux.a056_bricksbreaker.ui.navigation

import androidx.navigation.NamedNavArgument

/**
 * A sealed class that represents the different screens in the application.
 *
 * @property route The route for the screen.
 * @property navArguments The navigation arguments for the screen.
 */
sealed class BricksBreakerScreen(
    val route: String,
    val navArguments: List<NamedNavArgument> = emptyList(),
    val routeWithArgs: String = "",
    val titleRes: Int = -1,
) {
    data object Home : BricksBreakerScreen(
        route = "home",
        titleRes = -1
    )
    data object Game : BricksBreakerScreen(
        route = "game",
        titleRes = -1
    )
    data object Score : BricksBreakerScreen(
        route = "score",
        titleRes = -1
    )
}