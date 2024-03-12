package io.wso2.android.api_authenticator.sdk.sample.util

object Config {
    private const val BASE_URL: String = "https://10.0.2.2:9443"
    private const val CLIENT_ID: String = "DemBfWSfjhJO2ieDeI67urt7O_0a"
    private const val REDIRECT_URI: String = "https://example-app.com/redirect"
    private const val SCOPE: String = "openid internal_login"

    fun getBaseUrl(): String {
        return BASE_URL
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
}
