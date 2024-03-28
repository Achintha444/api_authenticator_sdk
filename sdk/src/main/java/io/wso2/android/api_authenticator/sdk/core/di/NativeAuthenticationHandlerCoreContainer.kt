package io.wso2.android.api_authenticator.sdk.core.di

import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.core_types.native_authentication_handler.impl.NativeAuthenticationHandlerCore
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.google_native_authentication_handler.GoogleNativeAuthenticationHandlerManager
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.google_native_authentication_handler.impl.GoogleNativeAuthenticationHandlerManagerImpl

/**
 * Dependency Injection container for the [NativeAuthenticationHandlerCore] class
 */
internal object NativeAuthenticationHandlerCoreContainer {
    /**
     * Get the [GoogleNativeAuthenticationHandlerManager] instance
     *
     * @param authenticationCoreConfig [AuthenticationCoreConfig] to get the Google Web Client ID
     *
     * @return [GoogleNativeAuthenticationHandlerManager] instance
     */
    internal fun getGoogleNativeAuthenticationHandlerManager(
        authenticationCoreConfig: AuthenticationCoreConfig,
    ): GoogleNativeAuthenticationHandlerManager =
        GoogleNativeAuthenticationHandlerManagerImpl.getInstance(
            authenticationCoreConfig,
            GoogleNativeAuthenticationHandlerManagerImplContainer
                .getGoogleNativeAuthenticationHandlerManagerImplRequestBuilder()
        )
}
