package io.wso2.android.api_authenticator.sdk.petcare.features.login.impl.mapper

import io.wso2.android.api_authenticator.sdk.petcare.features.login.domain.model.error.AuthenticationError

fun Throwable.toAuthenticationError(): AuthenticationError {
    val errorMessage: String = if (this.message != null) this.message!! else "Something went wrong"
    return AuthenticationError(errorMessage, this)
}
