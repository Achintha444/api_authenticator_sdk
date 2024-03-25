package io.wso2.android.api_authenticator.sdk.provider.di

import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.impl.AuthenticationCore
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.token.TokenProviderManager
import io.wso2.android.api_authenticator.sdk.provider.providers.token.impl.TokenProviderImpl
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.token.impl.TokenProviderManagerImpl

/**
 * Dependency Injection container for the [TokenProviderImpl] class.
 */
internal object TokenProviderImplContainer {
    /**
     * Get the instance of the [AuthenticationCoreDef].
     *
     * @return [AuthenticationCoreDef] instance
     */
    internal fun getAuthenticationCoreDef(): AuthenticationCoreDef? =
        AuthenticationCore.getInstance()
    /**
     * Get the instance of the [TokenProviderManager].
     *
     * @param authenticationCore [AuthenticationCoreDef] instance
     *
     * @return [TokenProviderManager] instance
     */
    internal fun getTokenProviderManager(authenticationCore: AuthenticationCoreDef)
            : TokenProviderManager = TokenProviderManagerImpl.getInstance(authenticationCore)
}