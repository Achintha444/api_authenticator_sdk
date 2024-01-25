package io.wso2.android.api_authenticator.sdk.models.auth_params

import io.wso2.android.api_authenticator.sdk.util.JsonUtil

/**
 * Authenticator parameters class
 */
abstract class AuthParams(
    /**
     * Username of the user - For Basic Authenticator
     */
    open val username: String? = null,
    /**
     * Password of the user - For Basic Authenticator
     */
    open val password: String? = null,
    /**
     * access token retrieved from the Google authenticator - For Google Authenticator
     */
    open val accessToken: String? = null,
    /**
     * id token retrieved from the Google authenticator - For Google Authenticator
     */
    open val idToken: String? = null,
    /**
     * Code retrieved from the authenticator application - For TOTP Authenticator
     */
    open val totp: String? = null,
    /**
     * Token response retrieved from the passkey authenticator - For Passkey Authenticator
     * TODO: Improve the comment
     */
    open val tokenResponse: String? = null
) {
    override fun toString(): String {
        return JsonUtil.getJsonString(this)
    }

    /**
     * Get the parameter body for the authenticator to be sent to the server
     */
    abstract fun getParameterBodyAuthenticator(): LinkedHashMap<String, String>
}
