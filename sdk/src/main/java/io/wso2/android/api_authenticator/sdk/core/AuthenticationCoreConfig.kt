package io.wso2.android.api_authenticator.sdk.core

import java.io.InputStream

/**
 * Holds the configuration related to the [AuthenticationCore].
 *
 * @property baseUrl Base url of the WSO2 identity server
 * @property clientId Client id of the application
 * @property scope Scope of the application (ex: openid profile email)
 * @property integrityToken Client attestation integrity token - optional
 * @property trustedCertificates Trusted certificates of the WSO2 identity server(in the PEM format)
 * as a [InputStream] - optional. If not provided, a less secure http client will be used, which
 * bypasses the certificate validation. `This is not recommended for production`.
 */
class AuthenticationCoreConfig (
    private val baseUrl: String,
    private val redirectUri: String,
    private val clientId: String,
    private val scope: String,
    private val integrityToken: String? = null,
    private val trustedCertificates: InputStream? = null
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
     * @return Trusted certificates of the WSO2 identity server(in the PEM format) as a [InputStream].
     */
    fun getTrustedCertificates(): InputStream? {
        return trustedCertificates
    }
}
