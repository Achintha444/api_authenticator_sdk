package io.wso2.android.api_authenticator.sdk.providers.di

import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.impl.AuthenticationCore
import io.wso2.android.api_authenticator.sdk.providers.authentication_provider.AuthenticationStateHandler

/**
 * Dependency Injection container for the [AuthenticationProvider] class.
 */
internal object AuthenticationProviderContainer {

    /**
     * Get the instance of the [AuthenticationCoreDef].
     *
     * @param authenticationCoreConfig Configuration of the [AuthenticationCoreDef]
     *
     * @return [AuthenticationCoreDef] instance
     */
    internal fun getAuthenticationCoreDef(authenticationCoreConfig: AuthenticationCoreConfig)
            : AuthenticationCoreDef {
        return AuthenticationCore.getInstance(authenticationCoreConfig)
    }

    /**
     * Get the instance of the [AuthenticationStateHandler].
     *
     * @return [AuthenticationStateHandler] instance
     */
    internal fun getAuthenticationStateHandler(): AuthenticationStateHandler {
        return AuthenticationStateHandler
    }
}