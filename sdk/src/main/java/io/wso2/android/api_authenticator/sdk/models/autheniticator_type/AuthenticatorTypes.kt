package io.wso2.android.api_authenticator.sdk.models.autheniticator_type

/**
 * Enum class for authenticator types
 *
 * @property authenticatorType Authenticator type value
 */
enum class AuthenticatorTypes(val authenticatorType: String) {
    /**
     * Basic authenticator type
     */
    BASIC_AUTHENTICATOR("Username & Password"),
    /**
     * Google native authenticator type
     */
    GOOGLE_NATIVE_AUTHENTICATOR("Google"),
    /**
     * Google redirect authenticator type
     */
    GOOGLE_REDIRECT_AUTHENTICATOR("Google"),
    /**
     * TOTP authenticator type
     */
    TOTP_AUTHENTICATOR("TOTP"),
    /**
     * Passkey authenticator type
     */
    PASSKEY_AUTHENTICATOR("Passkey"),
    /**
     * OpenID Connect authenticator type
     */
    OPENID_CONNECT_AUTHENTICATOR("openidconnect")
}
