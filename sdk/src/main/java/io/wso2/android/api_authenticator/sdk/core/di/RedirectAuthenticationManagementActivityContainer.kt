package io.wso2.android.api_authenticator.sdk.core.di

import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.redirect.RedirectAuthenticationHandlerManager
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.redirect.impl.RedirectAuthenticationHandlerManagerImpl
import io.wso2.android.api_authenticator.sdk.core.ui.RedirectAuthenticationManagementActivity

/**
 * Dependency Injection container for [RedirectAuthenticationManagementActivity]
 */
object RedirectAuthenticationManagementActivityContainer {
    /**
     * Get the [RedirectAuthenticationHandlerManager] instance
     *
     * @return [RedirectAuthenticationHandlerManager] instance
     */
    fun getRedirectAuthenticationHandlerManager(): RedirectAuthenticationHandlerManager =
        RedirectAuthenticationHandlerManagerImpl.getInstance()
}
