package io.wso2.android.api_authenticator.sdk.core.di

import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.passkey.impl.PasskeyAuthenticationHandlerManagerImplRequestBuilder
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.passkey.impl.PasskeyAuthenticationHandlerManagerImpl

/**
 * Container class to di the [PasskeyAuthenticationHandlerManagerImpl]
 * This class is responsible for providing the [PasskeyAuthenticationHandlerManagerImplRequestBuilder] instance
 */
object PasskeyAuthenticationHandlerManagerImplContainer {
    /**
     * Get the [PasskeyAuthenticationHandlerManagerImplRequestBuilder] instance
     *
     * @return [PasskeyAuthenticationHandlerManagerImplRequestBuilder] instance
     */
    internal fun getPasskeyAuthenticationHandlerManagerImplRequestBuilder()
            : PasskeyAuthenticationHandlerManagerImplRequestBuilder =
        PasskeyAuthenticationHandlerManagerImplRequestBuilder
}
