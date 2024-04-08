package io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.screens.auth_screen.components

import androidx.compose.runtime.Composable
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorTypes
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess
import io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.util.common_component.ContinueText
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.components.PasskeyAuth

@Composable
internal fun AuthUI(authenticationFlow: AuthenticationFlowNotSuccess) {

    val authenticators: ArrayList<AuthenticatorType> = authenticationFlow.nextStep.authenticators

    // show basic auth screen if basic auth is available
    authenticators.forEach {
        when (it.authenticator) {
            AuthenticatorTypes.BASIC_AUTHENTICATOR.authenticatorType -> {
                BasicAuth(authenticatorType = it)
                if (authenticators.size > 1) {
                    ContinueText()
                }
            }
        }
    }

    authenticators.forEach {
        when (it.authenticator) {
            AuthenticatorTypes.GOOGLE_AUTHENTICATOR.authenticatorType -> {
                GoogleNativeAuth(authenticatorType = it)
            }

            AuthenticatorTypes.OPENID_CONNECT_AUTHENTICATOR.authenticatorType -> {
                OpenIdRedirectAuth(authenticatorType = it)
            }

            AuthenticatorTypes.GITHUB_REDIRECT_AUTHENTICATOR.authenticatorType -> {
                GithubAuth(authenticatorType = it)
            }

            AuthenticatorTypes.MICROSOFT_REDIRECT_AUTHENTICATOR.authenticatorType -> {
                MicrosoftAuth(authenticatorType = it)
            }

            AuthenticatorTypes.PASSKEY_AUTHENTICATOR.authenticatorType -> {
                PasskeyAuth(authenticatorType = it)
            }

            AuthenticatorTypes.TOTP_AUTHENTICATOR.authenticatorType -> {
                TotpAuth(authenticatorType = it)
            }

            else -> {}
        }
    }
}
