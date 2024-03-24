package io.wso2.android.api_authenticator.sdk.core.di

import io.wso2.android.api_authenticator.sdk.core.managers.authenticator.impl.AuthenticatorManagerImplRequestBuilder
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.authenticator_type_factory.AuthenticatorTypeFactory
import io.wso2.android.api_authenticator.sdk.models.http_client.http_client_builder.HttpClientBuilder
import okhttp3.OkHttpClient
import java.io.InputStream

/**
 * Dependency Injection container for the [AuthenticatorManagerImpl] class.
 */
object AuthenticatorManagerImplContainer {
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
    internal fun getClient(isDevelopment: Boolean?): OkHttpClient {
        return HttpClientBuilder.getHttpClientInstance(isDevelopment)
    }

    /**
     * Returns an instance of the [AuthenticatorTypeFactory] class.
     *
     * @return [AuthenticatorTypeFactory] instance.
     */
    internal fun getAuthenticatorTypeFactory(): AuthenticatorTypeFactory {
        return AuthenticatorTypeFactory
    }

    /**
     * Returns an instance of the [AuthenticatorManagerImplRequestBuilder] class.
     *
     * @return [AuthenticatorManagerImplRequestBuilder] instance.
     */
    internal fun getAuthenticatorManagerImplRequestBuilder(): AuthenticatorManagerImplRequestBuilder {
        return AuthenticatorManagerImplRequestBuilder
    }

    /**
     * Returns the authn url of the WSO2 identity server.
     *
     * @property authnUrl The authorization url.
     *
     * @return The authnUrl url.
     */
    internal fun getAuthnUrl(authnUrl: String): String {
        return authnUrl
    }
}
