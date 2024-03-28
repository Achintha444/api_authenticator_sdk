package io.wso2.android.api_authenticator.sdk.core.di

import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.google_native_authentication_handler.impl.GoogleNativeAuthenticationHandlerManagerImplRequestBuilder
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.google_native_authentication_handler.impl.GoogleNativeAuthenticationHandlerManagerImpl

/**
 * Container class to di the [GoogleNativeAuthenticationHandlerManagerImpl]
 */
object GoogleNativeAuthenticationHandlerManagerImplContainer {
    /**
     * Get the [GoogleNativeAuthenticationHandlerManagerImplRequestBuilder] instance
     *
     * @return [GoogleNativeAuthenticationHandlerManagerImplRequestBuilder] instance
     */
    internal fun getGoogleNativeAuthenticationHandlerManagerImplRequestBuilder()
            : GoogleNativeAuthenticationHandlerManagerImplRequestBuilder =
        GoogleNativeAuthenticationHandlerManagerImplRequestBuilder
}
