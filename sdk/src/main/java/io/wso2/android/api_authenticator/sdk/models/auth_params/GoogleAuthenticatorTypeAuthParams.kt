package io.wso2.android.api_authenticator.sdk.models.auth_params

/**
 * Authenticator parameters class - For Google Authenticator
 */
data class GoogleAuthenticatorTypeAuthParams(
    override val accessToken: String?,
    override val idToken: String?,
): AuthParams(
    accessToken = accessToken,
    idToken = idToken
)