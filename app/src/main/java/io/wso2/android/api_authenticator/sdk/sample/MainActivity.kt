package io.wso2.android.api_authenticator.sdk.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import io.wso2.android.api_authenticator.sdk.providers.authentication_provider.AuthenticationProvider
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.GoogleAuthenticationRepository
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.ProviderRepository
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.AuthScreenViewModel
import io.wso2.android.api_authenticator.sdk.sample.ui.theme.Api_authenticator_sdkTheme
import io.wso2.android.api_authenticator.sdk.sample.util.Event
import io.wso2.android.api_authenticator.sdk.sample.util.EventBus
import io.wso2.android.api_authenticator.sdk.sample.util.navigation.NavDestination
import io.wso2.android.api_authenticator.sdk.sample.util.navigation.NavGraph
import io.wso2.android.api_authenticator.sdk.sample.util.navigation.NavigationViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Api_authenticator_sdkTheme {
                val lifecycle = LocalLifecycleOwner.current.lifecycle
                val navigationController = rememberNavController()

                LaunchedEffect(key1 = lifecycle) {
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        EventBus.events.collect { event ->
                            when (event) {
                                is Event.Toast -> {
                                    // Show toast
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    NavigationViewModel.navigationEvents.collect {
                        when (it) {
                            is NavigationViewModel.Companion.NavigationEvent.NavigateBack -> {
                                navigationController.popBackStack()
                            }

                            is NavigationViewModel.Companion.NavigationEvent.NavigateToLanding -> {
                                navigationController.navigate(NavDestination.LandingScreen)
                            }

                            is NavigationViewModel.Companion.NavigationEvent.NavigateToHome -> {
                                navigationController.navigate(NavDestination.HomeScreen)
                            }

                            is NavigationViewModel.Companion.NavigationEvent.NavigateToAuthWithData -> {
                                navigationController.navigate(
                                    "${NavDestination.AuthScreen}?authenticationFlow={authenticationFlow}"
                                        .replace(
                                            "{authenticationFlow}",
                                            newValue = it.data
                                        )
                                )
                            }
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.background
                                ),
                                radius = 1500f,
                                center = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                            )
                        ),
                    color = Color.Transparent
                ) {
                    NavGraph(navController = navigationController)
                }
            }
        }
    }
}
