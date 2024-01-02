package io.wso2.android.api_authenticator.sdk.models.auth_params

/**
 * Authenticator parameters class - For Basic Authenticator
 */
data class BasicAuthenticatorAuthParams(
    /**
     * Username of the user
     */
    override val username: String,
    /**
     * Password of the user
     */
    override val password: String
): AuthParams(
    username = username,
    password = password
)
