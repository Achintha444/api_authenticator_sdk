package io.wso2.android.api_authenticator.sdk.ui.di

import io.wso2.android.api_authenticator.sdk.provider.providers.authentication.AuthenticationProvider

/**
 * Dependency Injection container for [RedirectUriReceiverActivity]
 */
object RedirectUriReceiverActivityContainer {
    /**
     * Get the [AuthenticationProvider] instance
     */
    fun getAuthenticatorProvider(): AuthenticationProvider? {
        return AuthenticationProvider.getInstance()
    }
}