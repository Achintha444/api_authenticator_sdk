package io.wso2.android.api_authenticator.sdk.core.di

import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.redirect_authentication_handler.RedirectAuthenticationHandlerManager
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.redirect_authentication_handler.impl.RedirectAuthenticationHandlerManagerImpl
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authenticate_handler.AuthenticateHandlerProviderManager
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authenticate_handler.impl.AuthenticateHandlerProviderManagerImpl
import io.wso2.android.api_authenticator.sdk.provider.providers.authentication.impl.AuthenticationProviderImpl
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
