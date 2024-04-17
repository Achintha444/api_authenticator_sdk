package io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.screens.auth_screen.components

import androidx.compose.runtime.Composable
import io.wso2.android.api_authenticator.sdk.models.autheniticator.Authenticator
import io.wso2.android.api_authenticator.sdk.models.autheniticator.AuthenticatorTypes
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess
import io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.util.common_component.ContinueText

@Composable
internal fun AuthUI(authenticationFlow: AuthenticationFlowNotSuccess) {

    val authenticators: ArrayList<Authenticator> = authenticationFlow.nextStep.authenticators

    // show basic auth screen if basic auth is available
    authenticators.forEach {
        when (it.authenticator) {
            AuthenticatorTypes.BASIC_AUTHENTICATOR.authenticatorType -> {
                BasicAuth(authenticator = it)
                if (authenticators.size > 1) {
                    ContinueText()
                }
            }
        }
    }

    authenticators.forEach {
        when (it.authenticator) {
            AuthenticatorTypes.GOOGLE_AUTHENTICATOR.authenticatorType -> {
                GoogleNativeAuth(authenticator = it)
            }

            AuthenticatorTypes.OPENID_CONNECT_AUTHENTICATOR.authenticatorType -> {
                OpenIdRedirectAuth(authenticator = it)
            }

            AuthenticatorTypes.GITHUB_REDIRECT_AUTHENTICATOR.authenticatorType -> {
                GithubAuth(authenticator = it)
            }

            AuthenticatorTypes.MICROSOFT_REDIRECT_AUTHENTICATOR.authenticatorType -> {
                MicrosoftAuth(authenticator = it)
            }

            AuthenticatorTypes.PASSKEY_AUTHENTICATOR.authenticatorType -> {
                PasskeyAuth(authenticator = it)
            }

            AuthenticatorTypes.TOTP_AUTHENTICATOR.authenticatorType -> {
                TotpAuth(authenticator = it)
            }

            else -> {}
        }
    }
}
