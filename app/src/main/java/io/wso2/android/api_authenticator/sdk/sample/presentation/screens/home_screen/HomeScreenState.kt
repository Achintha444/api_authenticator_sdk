package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.home_screen

import io.wso2.android.api_authenticator.sdk.sample.domain.model.UserDetails

data class HomeScreenState(
    val isLoading: Boolean = false,
    val error: String = "",
    val user: UserDetails? = null
)
