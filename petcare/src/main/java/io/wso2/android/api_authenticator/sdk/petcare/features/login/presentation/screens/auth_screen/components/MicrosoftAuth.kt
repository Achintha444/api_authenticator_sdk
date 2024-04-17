package io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.screens.auth_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import io.wso2.android.api_authenticator.sdk.models.autheniticator.Authenticator
import io.wso2.android.api_authenticator.sdk.petcare.R
import io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.screens.auth_screen.AuthScreenViewModel
import io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.util.common_component.AuthButton

@Composable
internal fun MicrosoftAuth(
    viewModel: AuthScreenViewModel = hiltViewModel(),
    authenticator: Authenticator
) {
    MicrosoftAuthComponent(
        onSubmit = {
            viewModel.authenticateWithGithubRedirect(authenticator.authenticatorId)
        }
    )
}

@Composable
fun MicrosoftAuthComponent(
    onSubmit: () -> Unit
) {
    AuthButton(
        onSubmit = onSubmit,
        label = "Continue with Microsoft",
        imageResource = R.drawable.microsoft,
        imageContextDescription = "Microsoft"
    )
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun MicrosoftAuthPreview() {
    MicrosoftAuthComponent(onSubmit = {})
}
