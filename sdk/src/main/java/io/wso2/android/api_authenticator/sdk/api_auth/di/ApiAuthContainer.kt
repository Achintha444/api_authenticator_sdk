package io.wso2.android.api_authenticator.sdk.api_auth.di

import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.impl.AuthenticationCore
import io.wso2.android.api_authenticator.sdk.api_auth.ApiAuth

/**
 * Dependency Injection container for the [ApiAuth] class.
 */
internal object ApiAuthContainer {
    /**
     * Get the instance of the [AuthenticationCoreDef].
     *
     * @param authenticationCoreConfig Configuration of the [AuthenticationCoreDef]
     *
     * @return [AuthenticationCoreDef] instance
     */
    internal fun getAuthenticationCoreDef(authenticationCoreConfig: AuthenticationCoreConfig)
            : AuthenticationCoreDef = AuthenticationCore.getInstance(authenticationCoreConfig)
}