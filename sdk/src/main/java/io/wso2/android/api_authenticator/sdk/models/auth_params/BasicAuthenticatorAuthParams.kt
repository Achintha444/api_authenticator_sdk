package io.wso2.android.api_authenticator.sdk.models.auth_params

/**
 * Authenticator parameters class - For Basic Authenticator
 */
data class BasicAuthenticatorAuthParams(
    override val username: String,
    override val password: String
): AuthParams(
    username = username,
    password = password
)
