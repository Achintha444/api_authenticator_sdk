package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.components

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.sample.R
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.AuthScreenViewModel

@Composable
internal fun GoogleNativeAuth(
    viewModel: AuthScreenViewModel = hiltViewModel(),
    authenticatorType: AuthenticatorType
) {
    val launcher: ActivityResultLauncher<Intent> = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.handleGoogleNativeLegacyAuthenticateResult(result)
    }

    GoogleNativeAuthComponent(
        onSubmit = {
            viewModel.authenticateWithGoogleNativeLegacy(
                authenticatorType.authenticatorId,
                launcher
            )
        }
    )
}

@Composable
fun GoogleNativeAuthComponent(
    onSubmit: () -> Unit
) {
    Button(
        onClick = onSubmit,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = stringResource(id = R.string.screens_auth_screen_google_login))
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun GoogleNativeAuthPreview() {
    GoogleNativeAuthComponent(onSubmit = {})
}
