package io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.screens.auth_screen

import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess

data class AuthScreenState(
    val isLoading: Boolean = false,
    val authenticationFlow: AuthenticationFlowNotSuccess? = null,
    val error: String = ""
)
