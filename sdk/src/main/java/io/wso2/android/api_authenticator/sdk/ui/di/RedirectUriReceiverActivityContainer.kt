package io.wso2.android.api_authenticator.sdk.ui.di

import io.wso2.android.api_authenticator.sdk.provider.providers.authentication.AuthenticationProvider
import io.wso2.android.api_authenticator.sdk.provider.providers.authentication.impl.AuthenticationProviderImpl
import io.wso2.android.api_authenticator.sdk.ui.RedirectUriReceiverActivity

/**
 * Dependency Injection container for [RedirectUriReceiverActivity]
 */
object RedirectUriReceiverActivityContainer {
    /**
     * Get the [AuthenticationProviderImpl] instance
     */
    fun getAuthenticatorProvider(): AuthenticationProvider? {
        return AuthenticationProviderImpl.getInstance()
    }
}