package io.wso2.android.api_authenticator.sdk.sample.data.impl.repository

import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.providers.authentication.AuthenticationProvider
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.AuthenticationProviderRepository
import io.wso2.android.api_authenticator.sdk.sample.util.Config
import javax.inject.Inject

class AuthenticationProviderRepositoryImpl @Inject constructor() :
    AuthenticationProviderRepository {

    private val authenticationProvider = AuthenticationProvider.getInstance(
        AuthenticationCoreConfig(
            Config.getBaseUrl(),
            Config.getRedirectUri(),
            Config.getClientId(),
            Config.getScope(),
            isDevelopment = true
        )
    )

    override fun getAuthenticationManager(): AuthenticationProvider {
        return authenticationProvider
    }
}
