package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.wso2.android.api_authenticator.sdk.sample.R
import io.wso2.android.api_authenticator.sdk.sample.ui.theme.Api_authenticator_sdkTheme

@Composable
internal fun BasicAuth(
    onLoginClicked: (username: String, password: String) -> Unit,
) {
    BasicAuthComponent(
        onLoginClicked = onLoginClicked
    )
}

@Composable
fun BasicAuthComponent(
    onLoginClicked: (username: String, password: String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = {
                Text(
                    text = stringResource(R.string.screens_auth_screen_basic_auth_username)
                )
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
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
                .fillMaxWidth(0.8f),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth(0.8f)
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
            onLoginClicked = { _, _ -> }
        )
    }
}
