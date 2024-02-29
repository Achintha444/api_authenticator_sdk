package io.wso2.android.api_authenticator.sdk.sample.domain.model.error

data class AuthenticationError(
    val errorMessage: String,
    val t: Throwable? = null
)
