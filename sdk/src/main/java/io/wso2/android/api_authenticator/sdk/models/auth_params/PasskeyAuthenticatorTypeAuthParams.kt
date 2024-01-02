package io.wso2.android.api_authenticator.sdk.models.auth_params

/**
 * Authenticator parameters class - For Passkey Authenticator
 */
data class PasskeyAuthenticatorTypeAuthParams(
    /**
     * Token response retrieved from the passkey authenticator
     * TODO: Improve the comment
     *
     */
    override val tokenResponse: String
): AuthParams(
    tokenResponse = tokenResponse
)
