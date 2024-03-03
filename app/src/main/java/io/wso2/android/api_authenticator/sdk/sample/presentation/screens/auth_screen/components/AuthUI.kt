package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.components

import androidx.compose.runtime.Composable
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.BasicAuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.TotpAuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authorize_flow.AuthorizeFlowNotSuccess

@Composable
internal fun AuthUI(
    authorizeFlow: AuthorizeFlowNotSuccess
) {
    authorizeFlow.nextStep.authenticators.forEach {
        when (it.authenticator) {
            BasicAuthenticatorType.AUTHENTICATOR_TYPE -> {
                BasicAuth()
            }

            TotpAuthenticatorType.AUTHENTICATOR_TYPE -> {
                TotpAuth()
            }
        }
    }
}
