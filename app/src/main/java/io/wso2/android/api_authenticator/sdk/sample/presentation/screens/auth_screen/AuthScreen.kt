package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.wso2.android.api_authenticator.sdk.models.authorize_flow.AuthorizeFlow
import io.wso2.android.api_authenticator.sdk.sample.R
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.components.AuthUI
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.components.BasicAuth
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.home_screen.HomeScreenContent
import io.wso2.android.api_authenticator.sdk.sample.presentation.util.common_component.LoadingDialog
import io.wso2.android.api_authenticator.sdk.sample.presentation.util.common_component.LogoSmall
import io.wso2.android.api_authenticator.sdk.sample.ui.theme.Api_authenticator_sdkTheme

@Composable
internal fun AuthScreen(
    viewModel: AuthScreenViewModel = hiltViewModel(),
    authorizeFlow: AuthorizeFlow
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = authorizeFlow) {
        viewModel.setAuthorizeFlow(authorizeFlow)
    }
    AuthScreenContent(state.value)
}

@Composable
fun AuthScreenContent(
    state: AuthScreenState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoadingDialog(isLoading = state.isLoading)
        LogoSmall()
        Spacer(modifier = Modifier.height(32.dp))
        state.authorizeFlow?.let { authorizeFlow ->
            AuthUI(authorizeFlow)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun AuthScreenPreview() {
    Api_authenticator_sdkTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LogoSmall()
            Spacer(modifier = Modifier.height(32.dp))
            BasicAuth()
        }
    }
}