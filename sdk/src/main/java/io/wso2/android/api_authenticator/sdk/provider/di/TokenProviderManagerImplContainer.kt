package io.wso2.android.api_authenticator.sdk.provider.di

import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.impl.AuthenticationCore

/**
 * Dependency Injection container for the [TokenProviderManagerImpl] class.
 */
internal object TokenProviderManagerImplContainer {
    /**
     * Get the instance of the [AuthenticationCoreDef].
     *
     * @return [AuthenticationCoreDef] instance
     */
    internal fun getAuthenticationCoreDef(): AuthenticationCoreDef? {
        return AuthenticationCore.getInstance()
    }
}