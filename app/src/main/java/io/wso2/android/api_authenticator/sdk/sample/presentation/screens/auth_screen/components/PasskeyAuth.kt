package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
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

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
internal fun PasskeyAuth(
    viewModel: AuthScreenViewModel = hiltViewModel(),
    authenticatorType: AuthenticatorType
) {
    PasskeyAuthComponent(
        onSubmit = {
            viewModel.authenticateWithPasskey(authenticatorType.authenticatorId)
        }
    )
}

@Composable
fun PasskeyAuthComponent(
    onSubmit: () -> Unit
) {
    Button(
        onClick = onSubmit,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = stringResource(id = R.string.screens_auth_screen_passkey_login))
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PasskeyAuthPreview() {
    PasskeyAuthComponent(onSubmit = {})
}
