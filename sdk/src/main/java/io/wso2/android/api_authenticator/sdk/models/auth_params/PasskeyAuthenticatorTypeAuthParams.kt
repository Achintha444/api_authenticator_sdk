package io.wso2.android.api_authenticator.sdk.models.auth_params

/**
 * Authenticator parameters class - For Passkey Authenticator
 */
data class PasskeyAuthenticatorTypeAuthParams(
    override val tokenResponse: String
): AuthParams(
    tokenResponse = tokenResponse
)
