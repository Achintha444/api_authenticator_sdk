package io.wso2.android.api_authenticator.sdk.models.auth_params

/**
 * Authenticator parameters class - For Google Authenticator
 */
data class GoogleAuthenticatorTypeAuthParams(
    /**
     * access token retrieved from the Google authenticator
     */
    override val accessToken: String?,
    /**
     * id token retrieved from the Google authenticator
     */
    override val idToken: String?,
): AuthParams(
    accessToken = accessToken,
    idToken = idToken
)