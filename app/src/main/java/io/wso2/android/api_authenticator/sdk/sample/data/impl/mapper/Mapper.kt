package io.wso2.android.api_authenticator.sdk.sample.data.impl.mapper

import io.wso2.android.api_authenticator.sdk.sample.domain.model.error.AuthenticationError

fun Throwable.toAuthenticationError(): AuthenticationError {
    val errorMessage: String = if (this.message != null) this.message!! else this.toString()
    return AuthenticationError(errorMessage, this)
}
