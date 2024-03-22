package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.AuthScreenViewModel

@Composable
internal fun OpenIdRedirectAuth(
    viewModel: AuthScreenViewModel = hiltViewModel(),
    authenticatorType: AuthenticatorType
) {
    OpenIdRedirectAuthComponent(
        onSubmit = {
            viewModel.authenticateWithRedirectUri(
                authenticatorType.authenticatorId
            )
        }
    )
}

@Composable
fun OpenIdRedirectAuthComponent(
    onSubmit: () -> Unit
) {
    Button(
        onClick = onSubmit,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "Sign in with OpenId Redirect")
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun OpenIdRedirectAuthPreview() {
    OpenIdRedirectAuthComponent(onSubmit = {})
}
