package io.wso2.android.api_authenticator.sdk.core.di

import io.wso2.android.api_authenticator.sdk.core_config.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.core_types.native_authentication_handler.impl.NativeAuthenticationHandlerCore
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.google_native.GoogleNativeAuthenticationHandlerManager
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.google_native.impl.GoogleNativeAuthenticationHandlerManagerImpl
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.google_native_legacy.impl.GoogleNativeLegacyAuthenticationHandlerManagerImpl
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.passkey.PasskeyAuthenticationHandlerManager
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.passkey.impl.PasskeyAuthenticationHandlerManagerImpl
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.redirect.RedirectAuthenticationHandlerManager
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.redirect.impl.RedirectAuthenticationHandlerManagerImpl

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

    /**
     * Get the [RedirectAuthenticationHandlerManager] instance
     *
     * @return [RedirectAuthenticationHandlerManager] instance
     */
    fun getRedirectAuthenticationHandlerManager(): RedirectAuthenticationHandlerManager =
        RedirectAuthenticationHandlerManagerImpl.getInstance()

    /**
     * Get the [PasskeyAuthenticationHandlerManager] instance
     *
     * @return [PasskeyAuthenticationHandlerManager] instance
     */
    fun getPasskeyAuthenticationHandlerManager(): PasskeyAuthenticationHandlerManager =
        PasskeyAuthenticationHandlerManagerImpl.getInstance(
            PasskeyAuthenticationHandlerManagerImplContainer
                .getPasskeyAuthenticationHandlerManagerImplRequestBuilder()
        )
}
