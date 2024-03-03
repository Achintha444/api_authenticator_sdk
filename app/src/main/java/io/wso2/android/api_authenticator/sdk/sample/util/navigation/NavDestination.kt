package io.wso2.android.api_authenticator.sdk.sample.util.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.wso2.android.api_authenticator.sdk.models.authorize_flow.AuthorizeFlow
import io.wso2.android.api_authenticator.sdk.models.authorize_flow.AuthorizeFlowNotSuccess
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.AuthScreen
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.AuthScreenViewModel
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.home_screen.HomeScreen
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.home_screen.HomeScreenViewModel
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.landing_screen.LandingScreen
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.landing_screen.LandingScreenViewModel
import java.net.URLDecoder
import java.net.URLEncoder

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
        composable("${NavDestination.AuthScreen}?authorizeFlow={authorizeFlow}") {
            val authorizeFlowString: String? = it.arguments?.getString("authorizeFlow")
            val authorizeFlow: AuthorizeFlow =
                AuthorizeFlowNotSuccess.fromJson(URLDecoder.decode(
                    authorizeFlowString!!, "utf-8")
                )
            AuthScreen(authorizeFlow = authorizeFlow)
        }
        composable(NavDestination.HomeScreen) {
            HomeScreen()
        }
    }
}
