package io.wso2.android.api_authenticator.sdk.sample.data.impl.repository

import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.providers.authentication_provider.AuthenticationProvider
import io.wso2.android.api_authenticator.sdk.providers.token_provider.TokenProvider
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.ProviderRepository
import io.wso2.android.api_authenticator.sdk.sample.util.Config
import javax.inject.Inject

class ProviderRepositoryImpl @Inject constructor() : ProviderRepository {
    private val authenticationProvider = AuthenticationProvider.getInstance(
        AuthenticationCoreConfig(
            Config.getBaseUrl(),
            Config.getRedirectUri(),
            Config.getClientId(),
            Config.getScope(),
            isDevelopment = true
        )
    )

    override fun getAuthenticationProvider(): AuthenticationProvider {
        return authenticationProvider
    }

    override fun getTokenProvider(): TokenProvider {
        return TokenProvider.getInstance()
    }
}
