package io.wso2.android.api_authenticator.sdk.core.di

import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.redirect.RedirectAuthenticationHandlerManager
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.redirect.impl.RedirectAuthenticationHandlerManagerImpl
import io.wso2.android.api_authenticator.sdk.core.ui.RedirectUriReceiverActivity

/**
 * Dependency Injection container for [RedirectUriReceiverActivity]
 */
object RedirectUriReceiverActivityContainer {
    /**
     * Get the [RedirectAuthenticationHandlerManager] instance
     *
     * @return [RedirectAuthenticationHandlerManager] instance
     */
    fun getRedirectAuthenticationHandlerManager(): RedirectAuthenticationHandlerManager =
        RedirectAuthenticationHandlerManagerImpl.getInstance()
}
