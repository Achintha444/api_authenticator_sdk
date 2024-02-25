package io.wso2.android.api_authenticator.sdk.core.di

import android.net.Uri
import io.wso2.android.api_authenticator.sdk.models.http_client.http_client_builder.HttpClientBuilder
import net.openid.appauth.AuthorizationServiceConfiguration
import okhttp3.OkHttpClient
import java.io.InputStream

/**
 * Dependency Injection container for the [AppAuthManagerImpl] class.
 */
internal object AppAuthManagerImplContainer {

    /**
     * Returns an instance of the [OkHttpClient] class, based on the given parameters.
     *
     * @property trustedCertificates The certificate(in the PEM format) of the WSO2 identity
     * server as a [InputStream] - optional. If not provided, a less secure http client will be
     * used, which bypasses the certificate validation. `This is not recommended for production`.
     *
     * @return [OkHttpClient] instance.
     */
    internal fun getCustomeTrustClient(trustedCertificates: InputStream? = null): OkHttpClient {
        return HttpClientBuilder.getHttpClientInstance(trustedCertificates)
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
            Uri.parse(tokenEndpoint)
        )
    }
}