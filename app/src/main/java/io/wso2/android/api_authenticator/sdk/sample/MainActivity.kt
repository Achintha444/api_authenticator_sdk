package io.wso2.android.api_authenticator.sdk.sample

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.wso2.android.api_authenticator.sdk.sample.R
import io.wso2.android.api_authenticator.sdk.sample.presentation.util.common_component.LogoImage
import io.wso2.android.api_authenticator.sdk.sample.ui.theme.Api_authenticator_sdkTheme
import io.wso2.android.api_authenticator.sdk.sample.util.Event
import io.wso2.android.api_authenticator.sdk.sample.util.EventBus
import io.wso2.android.api_authenticator.sdk.sample.util.NavGraph

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Api_authenticator_sdkTheme {
                val lifecycle = LocalLifecycleOwner.current.lifecycle
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
                    NavGraph(navController = rememberNavController())
                }
            }
        }
    }
}
