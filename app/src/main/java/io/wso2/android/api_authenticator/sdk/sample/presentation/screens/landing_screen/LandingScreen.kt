package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.landing_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.wso2.android.api_authenticator.sdk.sample.R
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.components.TotpAuth
import io.wso2.android.api_authenticator.sdk.sample.presentation.util.common_component.LoadingDialog
import io.wso2.android.api_authenticator.sdk.sample.presentation.util.common_component.LogoLarge
import io.wso2.android.api_authenticator.sdk.sample.ui.theme.Api_authenticator_sdkTheme

@Composable
internal fun LandingScreen(
    viewModel: LandingScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    LandingScreenContent(state.value, loginOnClick = viewModel::authorize)
}


@Composable
fun LandingScreenContent(
    state: LandingScreenState,
    loginOnClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoadingDialog(isLoading = state.isLoading)
        LogoLarge()
        LoginButton(Modifier, loginOnClick)
    }
}

@Composable
private fun LoginButton(modifier: Modifier = Modifier, onClcik: () -> Unit) {
    Button(
        modifier = modifier,
        onClick = onClcik
    ) {
        Text(text = stringResource(R.string.common_login))
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
