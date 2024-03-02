package io.wso2.android.api_authenticator.sdk.sample.util

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.home_screen.HomeScreen
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.home_screen.HomeScreenViewModel
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.landing_screen.LandingScreen
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.landing_screen.LandingScreenViewModel

object NavDestination {
    const val LandingScreen: String = LandingScreenViewModel.TAG
    const val HomeScreen: String = HomeScreenViewModel.TAG
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.LandingScreen
    ) {
        composable(NavDestination.LandingScreen) {
            LandingScreen()
        }
        composable(NavDestination.HomeScreen) {
            HomeScreen()
        }
    }
}
