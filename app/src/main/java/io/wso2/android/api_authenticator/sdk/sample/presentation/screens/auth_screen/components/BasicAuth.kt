package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.wso2.android.api_authenticator.sdk.models.auth_params.BasicAuthenticatorAuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.providers.authentication.AuthenticationState
import io.wso2.android.api_authenticator.sdk.sample.R
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.AuthScreenViewModel
import io.wso2.android.api_authenticator.sdk.sample.presentation.util.sendEvent
import io.wso2.android.api_authenticator.sdk.sample.ui.theme.Api_authenticator_sdkTheme
import io.wso2.android.api_authenticator.sdk.sample.util.Event
import io.wso2.android.api_authenticator.sdk.sample.util.navigation.NavigationViewModel
import kotlinx.coroutines.flow.update

@Composable
internal fun BasicAuth(
    viewModel: AuthScreenViewModel = hiltViewModel(),
    authenticatorType: AuthenticatorType
) {
    BasicAuthComponent(
        onLoginClick = { username, password ->
            viewModel.authenticateWithUsernamePassword(
                authenticatorType,
                username,
                password
            )
        }
    )
}

@Composable
fun BasicAuthComponent(
    onLoginClick: (username: String, password: String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Text(
            text = stringResource(id = R.string.screens_auth_screen_basic_auth_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.screens_auth_screen_basic_auth_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = {
                Text(
                    text = stringResource(R.string.screens_auth_screen_basic_auth_username)
                )
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    text = stringResource(R.string.screens_auth_screen_basic_auth_password)
                )
            },
            modifier = Modifier
                .fillMaxWidth(0.9f),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onLoginClick(username, password) },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Text(text = stringResource(R.string.screens_auth_screen_basic_auth_login))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun BasicAuthPreview() {
    Api_authenticator_sdkTheme {
        BasicAuthComponent(
            onLoginClick = { _, _ -> }
        )
    }
}
