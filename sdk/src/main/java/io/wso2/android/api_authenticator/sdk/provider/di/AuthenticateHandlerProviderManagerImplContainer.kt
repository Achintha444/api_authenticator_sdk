package io.wso2.android.api_authenticator.sdk.provider.di

import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication_state.AuthenticationStateProviderManager
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication_state.impl.AuthenticationStateProviderManagerImpl

/**
 * Dependency Injection container for [AuthenticationStateProviderManagerImpl]
 */
internal object AuthenticateHandlerProviderManagerImplContainer {
    /**
     * Get the [AuthenticationStateProviderManager] instance
     *
     * @param authenticationCore The [AuthenticationCoreDef] instance
     *
     * @return The [AuthenticationStateProviderManager] instance
     */
    internal fun getAuthenticateStateProviderManager(
        authenticationCore: AuthenticationCoreDef
    ): AuthenticationStateProviderManager {
        return AuthenticationStateProviderManagerImpl.getInstance(
            authenticationCore
        )
    }
}