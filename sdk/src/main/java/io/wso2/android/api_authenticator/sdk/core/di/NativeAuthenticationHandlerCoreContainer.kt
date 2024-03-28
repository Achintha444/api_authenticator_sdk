package io.wso2.android.api_authenticator.sdk.core.di

import android.os.Build
import androidx.annotation.RequiresApi
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.core_types.native_authentication_handler.impl.NativeAuthenticationHandlerCore
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.google_native_authentication_handler.GoogleNativeAuthenticationHandlerManager
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.google_native_authentication_handler.impl.GoogleNativeAuthenticationHandlerManagerImpl
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.google_native_legacy_authentication_handler.impl.GoogleNativeLegacyAuthenticationHandlerManagerImpl

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

    /**
     * Get the [GoogleNativeLegacyAuthenticationHandlerManagerImpl] instance
     *
     * @param authenticationCoreConfig [AuthenticationCoreConfig] to get the Google Web Client ID
     *
     * @return [GoogleNativeLegacyAuthenticationHandlerManagerImpl] instance
     */
    internal fun getGoogleNativeLegacyAuthenticationHandlerManager(
        authenticationCoreConfig: AuthenticationCoreConfig,
    ): GoogleNativeLegacyAuthenticationHandlerManagerImpl =
        GoogleNativeLegacyAuthenticationHandlerManagerImpl.getInstance(authenticationCoreConfig)
}
