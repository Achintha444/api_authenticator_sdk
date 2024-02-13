//package io.wso2.android.api_authenticator.sdk.sample.util
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.composable
//
//// Main navigation graph
//sealed class MainNavDestination {
//    object Home : MainNavDestination()
//    object MainActivity : MainNavDestination()
//}
//
//@Composable
//fun MainNavGraph(navController: NavHostController) {
//    NavHost(
//        modifier = modifier,
//        navController = navController,
//        startDestination = startDestination
//    ) {
//        composable(NavigationItem.Splash.route) {
//            SplashScreen(navController)
//        }
//        composable(NavigationItem.Login.route) {
//            LoginScreen(navController)
//        }
//    }
//}
