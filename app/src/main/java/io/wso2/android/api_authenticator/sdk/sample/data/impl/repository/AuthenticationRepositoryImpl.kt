package io.wso2.android.api_authenticator.sdk.sample.data.impl.repository

import android.content.Context
import arrow.core.Either
import io.wso2.android.api_authenticator.sdk.core_config.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.core_types.authentication.impl.AuthenticationCore
import io.wso2.android.api_authenticator.sdk.models.autheniticator.Authenticator
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow
import io.wso2.android.api_authenticator.sdk.sample.data.impl.mapper.toAuthenticationError
import io.wso2.android.api_authenticator.sdk.sample.domain.model.error.AuthenticationError
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.AuthenticationRepository
import io.wso2.android.api_authenticator.sdk.sample.util.Config
import javax.inject.Inject

/**
 * [AuthenticationRepositoryImpl] is the implementation of the [AuthenticationRepository]
 */
class AuthenticationRepositoryImpl @Inject constructor() : AuthenticationRepository {

    private val authenticationCore = AuthenticationCore.getInstance(
        AuthenticationCoreConfig(
            Config.getBaseUrl(),
            Config.getRedirectUri(),
            Config.getClientId(),
            Config.getScope(),
            isDevelopment = true
        )
    )

    override suspend fun authorize(): Either<AuthenticationError, AuthenticationFlow> {
        return Either.catch {
            authenticationCore.authorize()!!
        }.mapLeft {
            it.toAuthenticationError()
        }
    }

    override suspend fun authn(
        authenticator: Authenticator,
        authenticatorParameters: LinkedHashMap<String, String>
    ): Either<AuthenticationError, AuthenticationFlow> {
        return Either.catch {
            authenticationCore.authn(authenticator, authenticatorParameters)!!
        }.mapLeft {
            it.toAuthenticationError()
        }
    }

    override suspend fun getAccessToken(
        context: Context,
        authorizationCode: String
    ): Either<AuthenticationError, String> {
        return Either.catch {
//            authenticationCore.getAccessToken(
//                context,
//                authorizationCode
//            )!!
            "accessToken"
        }.mapLeft {
            it.toAuthenticationError()
        }
    }
}
