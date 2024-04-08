package io.wso2.android.api_authenticator.sdk.sample.util

object Config {
    private const val BASE_URL: String = "https://10.0.2.2:9443"
    private const val CLIENT_ID: String = "YOUR_CLIENT ID"
    private const val REDIRECT_URI: String = "wso2.apiauth.sample.android://login-callback"
    private const val SCOPE: String = "openid internal_login profile email"
    private const val GOOGLE_WEB_CLIENT_ID = "YOUR_GOOGLE_WEB_CLIENT_ID"

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

    fun getGoogleWebClientId(): String {
        return GOOGLE_WEB_CLIENT_ID
    }
}
