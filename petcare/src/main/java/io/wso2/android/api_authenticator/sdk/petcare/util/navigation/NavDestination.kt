package io.wso2.android.api_authenticator.sdk.petcare.util.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess
import io.wso2.android.api_authenticator.sdk.petcare.features.home.presentation.screens.home.HomeScreen
import io.wso2.android.api_authenticator.sdk.petcare.features.home.presentation.screens.home.HomeScreenViewModel
import io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.screens.auth_screen.AuthScreen
import io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.screens.auth_screen.AuthScreenViewModel
import io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.screens.landing_screen.LandingScreen
import io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.screens.landing_screen.LandingScreenViewModel
import java.net.URLDecoder

object NavDestination {
    const val LandingScreen: String = LandingScreenViewModel.TAG
    const val AuthScreen: String = AuthScreenViewModel.TAG
    const val HomeScreen: String = HomeScreenViewModel.TAG
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavDestination.HomeScreen
    ) {
        composable(NavDestination.LandingScreen) {
            LandingScreen()
        }
        composable("${NavDestination.AuthScreen}?authenticationFlow={authenticationFlow}") {
            val authenticationFlowString: String? = it.arguments?.getString("authenticationFlow")
            val authenticationFlow: AuthenticationFlow = AuthenticationFlowNotSuccess.fromJson(
                URLDecoder.decode(authenticationFlowString!!, "utf-8"))
            AuthScreen(authenticationFlow = authenticationFlow)
        }
        composable(NavDestination.HomeScreen) {
            HomeScreen()
        }
    }
}
