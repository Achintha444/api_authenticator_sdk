package io.wso2.android.api_authenticator.sdk.provider.di

import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.impl.AuthenticationCore
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication.impl.AuthenticationStateHandler

/**
 * Dependency Injection container for the [AuthenticationProviderManagerImpl] class.
 */
internal object AuthenticationProviderManagerImplContainer {
    /**
     * Get the instance of the [AuthenticationStateHandler].
     *
     * @return [AuthenticationStateHandler] instance
     */
    internal fun getAuthenticationStateHandler(): AuthenticationStateHandler {
        return AuthenticationStateHandler
    }
}