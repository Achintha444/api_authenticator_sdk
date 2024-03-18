package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.components

import androidx.compose.runtime.Composable
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.BasicAuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess

@Composable
internal fun AuthUI(
    authenticationFlow: AuthenticationFlowNotSuccess
) {
    authenticationFlow.nextStep.authenticators.forEach {
        when (it.authenticator) {
            BasicAuthenticatorType.AUTHENTICATOR_TYPE -> {
                BasicAuth(authenticatorType = it)
            }

            else -> {
                TotpAuth(authenticatorType = it)
            }
        }
    }
}
