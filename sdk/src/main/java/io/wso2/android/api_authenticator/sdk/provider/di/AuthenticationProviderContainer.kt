package io.wso2.android.api_authenticator.sdk.provider.di

import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.impl.AuthenticationCore
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication.AuthenticationProviderManager
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication.impl.AuthenticationProviderManagerImpl

/**
 * Dependency Injection container for the [AuthenticationProviderManager] class.
 */
object AuthenticationProviderContainer {
    /**
     * Get the instance of the [AuthenticationCoreDef].
     *
     * @param authenticationCoreConfig Configuration of the [AuthenticationCoreDef]
     *
     * @return [AuthenticationCoreDef] instance
     */
    internal fun getAuthenticationCoreDef(authenticationCoreConfig: AuthenticationCoreConfig)
            : AuthenticationCoreDef = AuthenticationCore.getInstance(authenticationCoreConfig)

    /**
     * Get the instance of the [AuthenticationProviderManager].
     *
     * @param authenticationCore [AuthenticationCoreDef] instance
     *
     * @return [AuthenticationProviderManager] instance
     */
    internal fun getAuthenticationProviderManager(authenticationCore: AuthenticationCoreDef)
            : AuthenticationProviderManager =
        AuthenticationProviderManagerImpl.getInstance(authenticationCore)
}