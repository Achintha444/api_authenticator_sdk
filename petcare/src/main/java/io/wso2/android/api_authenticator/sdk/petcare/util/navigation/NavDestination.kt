package io.wso2.android.api_authenticator.sdk.petcare.util.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.screens.landing_screen.LandingScreen
import io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.screens.landing_screen.LandingScreenViewModel

object NavDestination {
    const val LandingScreen: String = LandingScreenViewModel.TAG
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.LandingScreen
    ) {
        if (navController.currentDestination?.route != NavDestination.LandingScreen) {
            composable(NavDestination.LandingScreen) {
                LandingScreen()
            }
        }
//        composable("${NavDestination.AuthScreen}?authenticationFlow={authenticationFlow}") {
//            val authenticationFlowString: String? = it.arguments?.getString("authenticationFlow")
//            val authenticationFlow: AuthenticationFlow = AuthenticationFlowNotSuccess.fromJson(
//                URLDecoder.decode(authenticationFlowString!!, "utf-8"))
//            AuthScreen(authenticationFlow = authenticationFlow)
//        }
//        composable(NavDestination.HomeScreen) {
//            HomeScreen()
//        }
    }
}
