package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.components

import androidx.compose.runtime.Composable
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorTypes
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.BasicAuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticationCoreException

@Composable
internal fun AuthUI(
    authenticationFlow: AuthenticationFlowNotSuccess
) {
    authenticationFlow.nextStep.authenticators.forEach {
        when (it.authenticator) {
            AuthenticatorTypes.BASIC_AUTHENTICATOR.authenticatorType -> {
                BasicAuth(authenticatorType = it)
            }

            AuthenticatorTypes.OPENID_CONNECT_AUTHENTICATOR.authenticatorType -> {
                OpenIdRedirectAuth(authenticatorType = it)
            }

            else -> {
                TotpAuth(authenticatorType = it)
            }
        }
    }
}
