package io.wso2.android.api_authenticator.sdk.petcare.features.home.presentation.screens.profile

import io.wso2.android.api_authenticator.sdk.petcare.features.home.domain.models.UserDetails

data class ProfileScreenState(
    val isLoading: Boolean = false,
    val error: String = "",
    val user: UserDetails? = null,
)
