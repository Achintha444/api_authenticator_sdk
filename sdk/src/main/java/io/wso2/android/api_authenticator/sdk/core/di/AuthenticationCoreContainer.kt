package io.wso2.android.api_authenticator.sdk.core.di

import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.managers.app_auth.AppAuthManager
import io.wso2.android.api_authenticator.sdk.core.managers.app_auth.impl.AppAuthManagerImpl
import io.wso2.android.api_authenticator.sdk.core.managers.authenticator.AuthenticatorManager
import io.wso2.android.api_authenticator.sdk.core.managers.authn.AuthnManager
import io.wso2.android.api_authenticator.sdk.core.managers.authn.impl.AuthnManagerImpl

/**
 * Dependency Injection container for the [AuthenticationCore]
 */
internal object AuthenticationCoreContainer {

    /**
     * Returns an instance of the [AuthnManager] object, based on the given parameters.
     *
     * @property authenticationCoreConfig The [AuthenticationCoreConfig] instance.
     *
     * @return [AuthnManager] instance.
     */
    internal fun getAuthMangerInstance(
        authenticationCoreConfig: AuthenticationCoreConfig
    ): AuthnManager {
        return AuthnManagerImpl.getInstance(
            authenticationCoreConfig,
            AuthnManagerImplContainer.getClient(
                authenticationCoreConfig.getIsDevelopment()
            ),
            AuthnManagerImplContainer.getAuthenticationCoreRequestBuilder(),
            AuthnManagerImplContainer.getFlowManager(authenticationCoreConfig)
        )
    }

    /**
     * Returns an instance of the [AppAuthManager] object, based on the given parameters.
     *
     * @property authenticationCoreConfig The [AuthenticationCoreConfig] instance.
     *
     * @return [AppAuthManager] instance.
     */
    internal fun getAppAuthManagerInstance(
        authenticationCoreConfig: AuthenticationCoreConfig
    ): AppAuthManager {
        return AppAuthManagerImpl.getInstance(
            AppAuthManagerImplContainer.getCustomTrustClient(
                authenticationCoreConfig.getIsDevelopment()
            ),
            AppAuthManagerImplContainer.getClientId(
                authenticationCoreConfig.getClientId()
            ),
            AppAuthManagerImplContainer.getServiceConfig(
                authenticationCoreConfig.getAuthorizeUrl(),
                authenticationCoreConfig.getTokenUrl()
            )
        )
    }
}