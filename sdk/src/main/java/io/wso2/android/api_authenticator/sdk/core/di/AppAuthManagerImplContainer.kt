package io.wso2.android.api_authenticator.sdk.core.di

import android.net.Uri
import io.wso2.android.api_authenticator.sdk.models.http_client.http_client_builder.HttpClientBuilder
import net.openid.appauth.AuthorizationServiceConfiguration
import okhttp3.OkHttpClient

/**
 * Dependency Injection container for the [AppAuthManagerImpl] class.
 */
internal object AppAuthManagerImplContainer {

    /**
     * Returns an instance of the [OkHttpClient] class, based on the given parameters.
     *
     * @property isDevelopment The flag to check whether the app is in development mode or not.
     * If true, the [LessSecureHttpClient] instance will be returned. Otherwise, the default
     * [OkHttpClient] instance will be returned. Default value is `false`. It is not recommended to
     * keep this value as `true` in production environment.
     *
     * @return [OkHttpClient] instance.
     */
    internal fun getCustomTrustClient(isDevelopment: Boolean?): OkHttpClient {
        return HttpClientBuilder.getHttpClientInstance(isDevelopment)
    }

    /**
     * Returns the client ID passed as a parameter.
     *
     * @property clientId The client ID.
     *
     * @return The client ID.
     */
    internal fun getClientId(clientId: String): String {
        return clientId
    }

    /**
     * Returns the redirect URI passed as a parameter.
     *
     * @property redirectUri The redirect URI.
     *
     * @return The redirect URI.
     */
    internal fun getRedirectUri(redirectUri: String): Uri {
        return Uri.parse(redirectUri)
    }

    /**
     * Returns the [AuthorizationServiceConfiguration] instance, based on the given parameters.
     *
     * @property authorizeEndpoint The authorize endpoint.
     * @property tokenEndpoint The token endpoint.
     *
     * @return [AuthorizationServiceConfiguration] instance.
     */
    internal fun getServiceConfig(
        authorizeEndpoint: String,
        tokenEndpoint: String
    ): AuthorizationServiceConfiguration {
        return AuthorizationServiceConfiguration(
            Uri.parse(authorizeEndpoint),
            Uri.parse(tokenEndpoint),

        )
    }
}