package io.wso2.android.api_authenticator.sdk.sample.util

object Config {
    private const val BASE_URL: String = "https://is.wso2isdemo.com"
    private const val CLIENT_ID: String = "jrQYwLBIkGGpMQK9mt3q6Rufbooa"
    private const val REDIRECT_URI: String = "https://example-app.com/redirect"
    private const val SCOPE: String = "openid internal_login"
    private const val RESPONSE_TYPE: String = "code"
    private const val RESPONSE_MODE: String = "direct"

    fun getBaseUrl(): String {
        return BASE_URL
    }

    fun getAuthorizeUrl(): String {
        return "$BASE_URL/oauth2/authorize"
    }

    fun getAuthnUrl(): String {
        return "$BASE_URL/oauth2/authn"
    }

    fun getTokenUrl(): String {
        return "$BASE_URL/oauth2/token"
    }

    fun getClientId(): String {
        return CLIENT_ID
    }

    fun getRedirectUri(): String {
        return REDIRECT_URI
    }

    fun getScope(): String {
        return SCOPE
    }

    fun getResponseType(): String {
        return RESPONSE_TYPE
    }

    fun getResponseMode(): String {
        return RESPONSE_MODE
    }
}
