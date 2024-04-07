package io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.screens.landing_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.util.common_component.FooterImage
import io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.util.common_component.LandingPageLogo
import io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.util.common_component.LoadingDialog
import io.wso2.android.api_authenticator.sdk.petcare.ui.theme.Api_authenticator_sdkTheme
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.landing_screen.LandingScreenState

@Composable
internal fun LandingScreen(
    viewModel: LandingScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    LandingScreenContent(state.value, loginOnClick = viewModel::initializeAuthentication)
}


@Composable
fun LandingScreenContent(
    state: LandingScreenState,
    loginOnClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LoadingDialog(state.isLoading)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 96.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(-8.dp)
            ) {
                LandingPageLogo()
                Text(
                    text = "Helping you to take " + "\n good care  of your pets",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            LoginButton(Modifier, loginOnClick)
            FooterImage()
        }
    }
}

@Composable
private fun LoginButton(modifier: Modifier = Modifier, onClcik: () -> Unit) {
    Button(
        modifier = modifier,
        onClick = onClcik
    ) {
        Text(text = "Getting Started")
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun LandingScreenPreview() {
    Api_authenticator_sdkTheme {
        LandingScreenContent(
            LandingScreenState(
                isLoading = false
            ),
            loginOnClick = {}
        )
    }
}
