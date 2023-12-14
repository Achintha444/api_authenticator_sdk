package io.wso2.android.api_authenticator.sdk.models.auth_params

/**
 * Authenticator parameters class - For TOTP Authenticator
 */
data class TotpAuthenticatorTypeAuthParams (
    override val totp: String
): AuthParams(
    totp = totp
)
