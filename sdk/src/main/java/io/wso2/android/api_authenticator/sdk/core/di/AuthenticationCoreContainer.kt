package io.wso2.android.api_authenticator.sdk.core.di

import android.content.Context
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.managers.app_auth.AppAuthManager
import io.wso2.android.api_authenticator.sdk.core.managers.app_auth.impl.AppAuthManagerImpl
import io.wso2.android.api_authenticator.sdk.core.managers.authenticator.AuthenticatorManager
import io.wso2.android.api_authenticator.sdk.core.managers.authn.AuthnManager
import io.wso2.android.api_authenticator.sdk.core.managers.authn.impl.AuthnManagerImpl
import io.wso2.android.api_authenticator.sdk.core.managers.token.TokenManager
import io.wso2.android.api_authenticator.sdk.core.managers.token.TokenManagerFactory
import io.wso2.android.api_authenticator.sdk.core.managers.token.impl.TokenManagerImpl

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
            AppAuthManagerImplContainer.getRedirectUri(
                authenticationCoreConfig.getRedirectUri()
            ),
            AppAuthManagerImplContainer.getServiceConfig(
                authenticationCoreConfig.getAuthorizeUrl(),
                authenticationCoreConfig.getTokenUrl()
            )
        )
    }

    /**
     * Returns an instance of the [TokenManager] object, based on the given parameters.
     *
     * @property context The [Context] instance.
     *
     * @return [TokenManager] instance.
     */
    internal fun getTokenManagerInstance(context: Context): TokenManager {
        return TokenManagerFactory.getTokenManager(context)
    }
}