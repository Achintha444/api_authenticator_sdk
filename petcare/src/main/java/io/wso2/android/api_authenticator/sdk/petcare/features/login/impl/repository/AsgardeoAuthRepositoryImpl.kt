package io.wso2.android.api_authenticator.sdk.petcare.features.login.impl.repository

import io.wso2.android.api_authenticator.sdk.asgardeo_auth.AsgardeoAuth
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.petcare.features.login.domain.repository.AsgardeoAuthRepository
import io.wso2.android.api_authenticator.sdk.provider.providers.authentication.AuthenticationProvider
import io.wso2.android.api_authenticator.sdk.provider.providers.token.TokenProvider
import io.wso2.android.api_authenticator.sdk.sample.util.Config
import javax.inject.Inject

class AsgardeoAuthRepositoryImpl @Inject constructor() : AsgardeoAuthRepository {
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
