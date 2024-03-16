package io.wso2.android.api_authenticator.sdk.sample.domain.repository

import io.wso2.android.api_authenticator.sdk.providers.authentication.AuthenticationProvider

/**
 * Use as a repository to handle the authentication related operations using Providers package of the SDK
 */
interface AuthenticationProviderRepository {

    fun getAuthenticationManager(): AuthenticationProvider
}
