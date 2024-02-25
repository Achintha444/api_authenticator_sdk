package io.wso2.android.api_authenticator.sdk.core.di

import io.wso2.android.api_authenticator.sdk.core.managers.authenticator.AuthenticatorManager
import io.wso2.android.api_authenticator.sdk.core.managers.authn.impl.AuthnManagerImplRequestBuilder
import io.wso2.android.api_authenticator.sdk.core.managers.flow.FlowManager
import io.wso2.android.api_authenticator.sdk.core.managers.flow.impl.FlowManagerImpl
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.authenticator_type_factory.AuthenticatorTypeFactory
import io.wso2.android.api_authenticator.sdk.models.http_client.http_client_builder.HttpClientBuilder
import okhttp3.OkHttpClient
import java.io.InputStream

/**
 * Dependency Injection container for the [AuthnManagerImpl] class.
 */
internal object AuthnManagerImplContainer {

    /**
     * Returns an instance of the [OkHttpClient] class, based on the given parameters.
     *
     * @property trustedCertificates The certificate(in the PEM format) of the WSO2 identity
     * server as a [InputStream] - optional. If not provided, a less secure http client will be
     * used, which bypasses the certificate validation. `This is not recommended for production`.
     *
     * @return [OkHttpClient] instance.
     */
    internal fun getClient(trustedCertificates: InputStream? = null): OkHttpClient {
        return HttpClientBuilder.getHttpClientInstance(trustedCertificates)
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
     * Returns an instance of the [AuthnManagerImplRequestBuilder] class.
     *
     * @return [AuthnManagerImplRequestBuilder] instance.
     */
    internal fun getAuthenticationCoreRequestBuilder(): AuthnManagerImplRequestBuilder {
        return AuthnManagerImplRequestBuilder
    }

    /**
     * Returns an instance of the [FlowManager] class.
     *
     * @property authenticatorManager The [AuthenticatorManager] instance.
     *
     * @return [FlowManager] instance.
     */
    internal fun getFlowManager(
        authenticatorManager: AuthenticatorManager
    ): FlowManager {
        return FlowManagerImpl.getInstance(authenticatorManager)
    }
}
