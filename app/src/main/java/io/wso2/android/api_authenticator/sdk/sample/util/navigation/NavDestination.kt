package io.wso2.android.api_authenticator.sdk.sample.util.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.AuthScreen
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.AuthScreenViewModel
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.home_screen.HomeScreen
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.home_screen.HomeScreenViewModel
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.landing_screen.LandingScreen
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.landing_screen.LandingScreenViewModel
import java.net.URLDecoder

object NavDestination {
    const val LandingScreen: String = LandingScreenViewModel.TAG
    const val HomeScreen: String = HomeScreenViewModel.TAG
    const val AuthScreen: String = AuthScreenViewModel.TAG
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
        composable("${NavDestination.AuthScreen}?authenticationFlow={authenticationFlow}") {
            val authenticationFlowString: String? = it.arguments?.getString("authenticationFlow")
            val authenticationFlow: io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow =
                io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess.fromJson(URLDecoder.decode(
                    authenticationFlowString!!, "utf-8")
                )
            AuthScreen(authenticationFlow = authenticationFlow)
        }
        composable(NavDestination.HomeScreen) {
            HomeScreen()
        }
    }
}
