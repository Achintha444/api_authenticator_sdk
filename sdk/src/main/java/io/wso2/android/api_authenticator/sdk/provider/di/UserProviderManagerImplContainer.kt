package io.wso2.android.api_authenticator.sdk.provider.di

import io.wso2.android.api_authenticator.sdk.core.core_types.authentication.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.token.TokenProviderManager
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.token.impl.TokenProviderManagerImpl
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.user.impl.UserProviderManagerImpl

/**
 * Dependency Injection container for the [UserProviderManagerImpl] interface.
 */
internal object UserProviderManagerImplContainer {
    /**
     * Get the instance of the [TokenProviderManager].
     *
     * @param authenticationCore [AuthenticationCoreDef] instance
     *
     * @return [TokenProviderManager] instance
     */
    internal fun getTokenProviderManager(
        authenticationCore: AuthenticationCoreDef
    ): TokenProviderManager =
        TokenProviderManagerImpl.getInstance(authenticationCore)
}
