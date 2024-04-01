package io.wso2.android.api_authenticator.sdk.sample.data.impl.repository

import io.wso2.android.api_authenticator.sdk.asgardeo_auth.AsgardeoAuth
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.provider.providers.authentication.AuthenticationProvider
import io.wso2.android.api_authenticator.sdk.provider.providers.token.TokenProvider
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.ProviderRepository
import io.wso2.android.api_authenticator.sdk.sample.util.Config
import javax.inject.Inject

class ProviderRepositoryImpl @Inject constructor() : ProviderRepository {
    private val asgardeoAuth: AsgardeoAuth = AsgardeoAuth.getInstance(
        AuthenticationCoreConfig(
            Config.getBaseUrl(),
            Config.getRedirectUri(),
            Config.getClientId(),
            Config.getScope(),
            googleWebClientId = Config.getGoogleWebClientId(),
            isDevelopment = true
        )
    )

    override fun getAuthenticationProvider(): AuthenticationProvider =
        asgardeoAuth.getAuthenticationProvider()

    override fun getTokenProvider(): TokenProvider = asgardeoAuth.getTokenProvider()
}
