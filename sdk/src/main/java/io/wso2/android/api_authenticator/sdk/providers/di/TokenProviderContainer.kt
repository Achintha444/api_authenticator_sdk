package io.wso2.android.api_authenticator.sdk.providers.di

import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.impl.AuthenticationCore

/**
 * Dependency Injection container for the [TokenProvider] class.
 */
internal object TokenProviderContainer {
    /**
     * Get the instance of the [AuthenticationCoreDef].
     *
     * @param authenticationCoreConfig Configuration of the [AuthenticationCoreDef]
     *
     * @return [AuthenticationCoreDef] instance
     */
    internal fun getAuthenticationCoreDef()
            : AuthenticationCoreDef {
        return AuthenticationCore.getInstance()
    }
}