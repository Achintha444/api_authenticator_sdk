package io.wso2.android.api_authenticator.sdk.core_config.di

import io.wso2.android.api_authenticator.sdk.core_config.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core_config.managers.authentication_core_config_manager.AuthenticationCoreConfigManager
import io.wso2.android.api_authenticator.sdk.core_config.managers.authentication_core_config_manager.impl.AuthenticationCoreConfigManagerImpl
import io.wso2.android.api_authenticator.sdk.core_config.providers.authentication_core_config_provider.impl.AuthenticationCoreConfigProviderImpl

/**
 * Dependency Injection container for the [AuthenticationCoreConfigProviderImpl] class.
 */
object AuthenticationCoreConfigProviderImplContainer {
    /**
     * Provide an instance of the [AuthenticationCoreConfigManager] class.
     *
     * @param authenticationCoreConfig The [AuthenticationCoreConfig] instance.
     *
     * @return [AuthenticationCoreConfigManager] instance.
     */
    internal fun getAuthenticationCoreConfigManager(
        authenticationCoreConfig: AuthenticationCoreConfig
    ): AuthenticationCoreConfigManager =
        AuthenticationCoreConfigManagerImpl.getInstance(
            authenticationCoreConfigFactory = AuthenticationCoreConfigManagerImplContainer
                .getAuthenticationCoreConfigFactory(),
            discoveryManager = AuthenticationCoreConfigManagerImplContainer.getDiscoveryManager(
                authenticationCoreConfig = authenticationCoreConfig
            )
        )
}
