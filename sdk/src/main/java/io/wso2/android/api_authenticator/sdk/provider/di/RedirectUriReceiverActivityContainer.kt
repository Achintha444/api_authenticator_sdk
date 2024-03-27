package io.wso2.android.api_authenticator.sdk.provider.di

import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authenticate_handler.AuthenticateHandlerProviderManager
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authenticate_handler.impl.AuthenticateHandlerProviderManagerImpl
import io.wso2.android.api_authenticator.sdk.provider.providers.authentication.impl.AuthenticationProviderImpl
import io.wso2.android.api_authenticator.sdk.provider.ui.RedirectUriReceiverActivity

/**
 * Dependency Injection container for [RedirectUriReceiverActivity]
 */
object RedirectUriReceiverActivityContainer {
    /**
     * Get the [AuthenticationProviderImpl] instance
     */
    fun getAuthenticateHandlerProviderManager(): AuthenticateHandlerProviderManager? {
        return AuthenticateHandlerProviderManagerImpl.getInstance()
    }
}