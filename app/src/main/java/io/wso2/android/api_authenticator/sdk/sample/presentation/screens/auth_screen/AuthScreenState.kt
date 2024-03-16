package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen

import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess

data class AuthScreenState(
    val isLoading: Boolean = false,
    val authenticationFlow: io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess? = null,
    val error: String = ""
)
