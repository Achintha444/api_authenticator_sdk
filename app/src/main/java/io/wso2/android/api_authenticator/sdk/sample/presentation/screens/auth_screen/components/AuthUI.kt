package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.components

import androidx.compose.runtime.Composable
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorTypes
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess

@Composable
internal fun AuthUI(authenticationFlow: AuthenticationFlowNotSuccess) {
    authenticationFlow.nextStep.authenticators.forEach {
        when (it.authenticator) {
            AuthenticatorTypes.BASIC_AUTHENTICATOR.authenticatorType -> {
                BasicAuth(authenticatorType = it)
            }

            AuthenticatorTypes.GOOGLE_AUTHENTICATOR.authenticatorType -> {
                GoogleNativeAuth(authenticatorType = it)
            }

            AuthenticatorTypes.OPENID_CONNECT_AUTHENTICATOR.authenticatorType -> {
                OpenIdRedirectAuth(authenticatorType = it)
            }

            AuthenticatorTypes.GITHUB_REDIRECT_AUTHENTICATOR.authenticatorType -> {
                GithubNativeAuth(authenticatorType = it)
            }

            AuthenticatorTypes.PASSKEY_AUTHENTICATOR.authenticatorType -> {
                PasskeyAuth(authenticatorType = it)
            }

            else -> {
                TotpAuth(authenticatorType = it)
            }
        }
    }
}
