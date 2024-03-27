package io.wso2.android.api_authenticator.sdk.provider.di

import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication.impl.AuthenticationProviderManagerImpl
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication_state.AuthenticationStateProviderManager
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication_state.impl.AuthenticationStateProviderManagerImpl

/**
 * Dependency Injection container for the [AuthenticationProviderManagerImpl] class.
 */
internal object AuthenticationProviderManagerImplContainer {

    /**
     * Get the instance of the [AuthenticationStateProviderManager].
     *
     * @return [AuthenticationStateProviderManager] instance
     */
    internal fun getAuthenticationStateProviderManager(authenticationCore: AuthenticationCoreDef)
            : AuthenticationStateProviderManager =
        AuthenticationStateProviderManagerImpl.getInstance(authenticationCore)
}
