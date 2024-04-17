package io.wso2.android.api_authenticator.sdk.asgardeo_auth.di

import io.wso2.android.api_authenticator.sdk.asgardeo_auth.AsgardeoAuth
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.core_types.authentication.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.core_types.authentication.impl.AuthenticationCore
import io.wso2.android.api_authenticator.sdk.core.core_types.native_authentication_handler.NativeAuthenticationHandlerCoreDef
import io.wso2.android.api_authenticator.sdk.core.core_types.native_authentication_handler.impl.NativeAuthenticationHandlerCore

/**
 * Dependency Injection container for the [AsgardeoAuth] class.
 */
internal object AsgardeoAuthContainer {
    /**
     * Get the instance of the [AuthenticationCoreDef].
     *
     * @param authenticationCoreConfig Configuration of the [AuthenticationCoreDef]
     *
     * @return [AuthenticationCoreDef] instance
     */
    internal fun getAuthenticationCoreDef(authenticationCoreConfig: AuthenticationCoreConfig)
            : AuthenticationCoreDef = AuthenticationCore.getInstance(authenticationCoreConfig)

    /**
     * Get the instance of the [NativeAuthenticationHandlerCoreDef].
     *
     * @param authenticationCoreConfig Configuration of the [AuthenticationCoreDef]
     *
     * @return [NativeAuthenticationHandlerCoreDef] instance
     */
    internal fun getNativeAuthenticationHandlerCoreDef(authenticationCoreConfig: AuthenticationCoreConfig)
            : NativeAuthenticationHandlerCoreDef =
        NativeAuthenticationHandlerCore.getInstance(authenticationCoreConfig)
}
