package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen

import io.wso2.android.api_authenticator.sdk.models.authorize_flow.AuthorizeFlowNotSuccess

data class AuthScreenState(
    val isLoading: Boolean = false,
    val authorizeFlow: AuthorizeFlowNotSuccess? = null,
    val error: String = ""
)
