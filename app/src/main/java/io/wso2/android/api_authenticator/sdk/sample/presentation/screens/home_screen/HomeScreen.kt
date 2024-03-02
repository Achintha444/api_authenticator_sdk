package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.home_screen

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
import io.wso2.android.api_authenticator.sdk.sample.presentation.util.common_component.LoadingDialog
import io.wso2.android.api_authenticator.sdk.sample.presentation.util.common_component.LogoSmall
import io.wso2.android.api_authenticator.sdk.sample.ui.theme.Api_authenticator_sdkTheme

@Composable
internal fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    HomeScreenContent(state.value)
}

@Composable
fun HomeScreenContent(
    state: HomeScreenState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoadingDialog(isLoading = state.isLoading)
        LogoSmall()
        LoginSuccessMessage()
        LogoutButton(Modifier)
    }
}

@Composable
private fun LoginSuccessMessage() {
    Text(
        text = stringResource(R.string.screens_home_screen_login_success)
    )
}

@Composable
private fun LogoutButton(modifier: Modifier = Modifier) {
    Button(
        modifier = modifier,
        onClick = { /*TODO*/ }
    ) {
        Text(text = stringResource(R.string.common_logout))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun HomeScreenPreview() {
    Api_authenticator_sdkTheme {
        HomeScreenContent(
            HomeScreenState(
                isLoading = false
            )
        )
    }
}