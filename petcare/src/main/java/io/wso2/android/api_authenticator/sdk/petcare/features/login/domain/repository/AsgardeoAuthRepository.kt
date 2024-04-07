package io.wso2.android.api_authenticator.sdk.petcare.features.login.domain.repository

import io.wso2.android.api_authenticator.sdk.provider.providers.authentication.AuthenticationProvider
import io.wso2.android.api_authenticator.sdk.provider.providers.token.TokenProvider

/**
 * Use as a repository to handle the authentication related operations using Asgardeo authentication SDK.
 */
interface AsgardeoAuthRepository {

    fun getAuthenticationProvider(): AuthenticationProvider

    fun getTokenProvider(): TokenProvider
}
