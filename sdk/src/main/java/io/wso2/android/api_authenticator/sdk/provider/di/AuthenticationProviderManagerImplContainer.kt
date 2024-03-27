package io.wso2.android.api_authenticator.sdk.provider.di

import io.wso2.android.api_authenticator.sdk.core.core_types.authentication.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authenticate_handler.AuthenticateHandlerProviderManager
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authenticate_handler.impl.AuthenticateHandlerProviderManagerImpl
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication_state.AuthenticationStateProviderManager
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication.impl.AuthenticationProviderManagerImpl
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication_state.impl.AuthenticationStateProviderManagerImpl

/**
 * Dependency Injection container for the [AuthenticationProviderManagerImpl] class.
 */
internal object AuthenticationProviderManagerImplContainer {

    /**
     * Get the instance of the [AuthenticationStateProviderManager].
     *
     * @param authenticationCore The [AuthenticationCoreDef] instance
     *
     * @return [AuthenticationStateProviderManager] instance
     */
    internal fun getAuthenticationStateProviderManager(authenticationCore: AuthenticationCoreDef)
            : AuthenticationStateProviderManager =
        AuthenticationStateProviderManagerImpl.getInstance(authenticationCore)

    /**
     * Get the instance of the [AuthenticateHandlerProviderManager].
     *
     * @param authenticationCore The [AuthenticationCoreDef] instance
     *
     * @return [AuthenticateHandlerProviderManager] instance
     */
    internal fun getAuthenticationHandlerProviderManager(authenticationCore: AuthenticationCoreDef)
            : AuthenticateHandlerProviderManager =
        AuthenticateHandlerProviderManagerImpl.getInstance(
            authenticationCore,
            AuthenticateHandlerProviderManagerImplContainer.getAuthenticateStateProviderManager(
                authenticationCore
            )
        )
}
