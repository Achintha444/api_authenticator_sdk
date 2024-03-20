package io.wso2.android.api_authenticator.sdk.sample.domain.repository

import io.wso2.android.api_authenticator.sdk.providers.authentication_provider.AuthenticationProvider
import io.wso2.android.api_authenticator.sdk.providers.token_provider.TokenProvider

/**
 * Use as a repository to handle the authentication related operations using Providers package of the SDK
 */
interface ProviderRepository {

    fun getAuthenticationProvider(): AuthenticationProvider

    fun getTokenProvider(): TokenProvider
}
