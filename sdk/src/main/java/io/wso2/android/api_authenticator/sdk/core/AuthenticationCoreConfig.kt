package io.wso2.android.api_authenticator.sdk.core

import io.wso2.android.api_authenticator.sdk.models.http_client.LessSecureHttpClient
import okhttp3.OkHttpClient

/**
 * Holds the configuration related to the [AuthenticationCore].
 *
 * @property baseUrl Base url of the WSO2 identity server
 * @property clientId Client id of the application
 * @property scope Scope of the application (ex: openid profile email)
 * @property integrityToken Client attestation integrity token - optional
 * @property isDevelopment The flag to check whether the app is in development mode or not.
 * If true, the [LessSecureHttpClient] instance will be returned. Otherwise, the default
 * [OkHttpClient] instance will be returned. Default value is `false`. It is not recommended to
 * keep this value as `false` in production environment.
 */
class AuthenticationCoreConfig(
    private val baseUrl: String,
    private val redirectUri: String,
    private val clientId: String,
    private val scope: String,
    private val integrityToken: String? = null,
    private val isDevelopment: Boolean? = false
) {
    /**
     * @example https://localhost:9443/oauth2/authorize
     *
     * @return Authorization url of the WSO2 identity server.
     */
    fun getAuthorizeUrl(): String {
        return "$baseUrl/oauth2/authorize"
    }

    /**
     * @example https://localhost:9443/oauth2/authn
     *
     * @return Authentication url of the WSO2 identity server.
     */
    fun getAuthnUrl(): String {
        return "$baseUrl/oauth2/authn"
    }

    /**
     * @example https://localhost:9443/oauth2/token
     *
     * @return Token url of the WSO2 identity server.
     */
    fun getTokenUrl(): String {
        return "$baseUrl/oauth2/token"
    }

    /**
     * @example https://example-app.com/redirect
     *
     * @return Redirect uri of the application.
     */
    fun getRedirectUri(): String {
        return redirectUri
    }

    /**
     * @return Client id of the application.
     */
    fun getClientId(): String {
        return clientId
    }

    /**
     * @return Scope of the application (ex: openid profile email).
     */
    fun getScope(): String {
        return scope
    }

    /**
     * @return Client attestation integrity token.
     */
    fun getIntegrityToken(): String? {
        return integrityToken
    }

    /**
     * @return The flag to check whether the app is in development mode or not [Boolean].
     */
    fun getIsDevelopment(): Boolean? {
        return isDevelopment
    }
}
