package io.wso2.android.api_authenticator.sdk.provider.di

import io.wso2.android.api_authenticator.sdk.core.core_types.authentication.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.core_types.native_authentication_handler.NativeAuthenticationHandlerCoreDef
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication.AuthenticationProviderManager
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication.impl.AuthenticationProviderManagerImpl
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.user.UserProviderManager
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.user.impl.UserProviderManagerImpl
import io.wso2.android.api_authenticator.sdk.provider.providers.authentication.impl.AuthenticationProviderImpl

/**
 * Dependency Injection container for the [AuthenticationProviderImpl] class.
 */
internal object AuthenticationProviderImplContainer {
    /**
     * Get the instance of the [AuthenticationProviderManager].
     *
     * @param authenticationCore [AuthenticationCoreDef] instance
     * @param nativeAuthenticationHandlerCore [NativeAuthenticationHandlerCoreDef] instance
     *
     * @return [AuthenticationProviderManager] instance
     */
    internal fun getAuthenticationProviderManager(
        authenticationCore: AuthenticationCoreDef,
        nativeAuthenticationHandlerCore: NativeAuthenticationHandlerCoreDef
    ): AuthenticationProviderManager =
        AuthenticationProviderManagerImpl.getInstance(
            authenticationCore,
            AuthenticationProviderManagerImplContainer.getAuthenticationStateProviderManager(
                authenticationCore
            ),
            AuthenticationProviderManagerImplContainer.getAuthenticationHandlerProviderManager(
                authenticationCore,
                nativeAuthenticationHandlerCore
            )
        )

    /**
     * Get the instance of the [UserProviderManager].
     *
     * @param authenticationCore [AuthenticationCoreDef] instance
     *
     * @return [UserProviderManager] instance
     */
    internal fun getUserProviderManager(
        authenticationCore: AuthenticationCoreDef
    ): UserProviderManager =
        UserProviderManagerImpl.getInstance(
            authenticationCore,
            UserProviderManagerImplContainer.getTokenProviderManager(authenticationCore)
        )
}
