package io.wso2.android.api_authenticator.sdk.sample.data.impl.repository

import io.wso2.android.api_authenticator.sdk.sample.domain.model.error.AuthenticationError
import android.content.Context
import arrow.core.Either
import io.wso2.android.api_authenticator.sdk.core.impl.AuthenticationCore
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.AuthenticationRepository
import io.wso2.android.api_authenticator.sdk.sample.data.impl.mapper.toAuthenticationError
import io.wso2.android.api_authenticator.sdk.sample.util.Config
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor() : AuthenticationRepository {

    private val authenticationCore = AuthenticationCore.getInstance(
        AuthenticationCoreConfig(
            Config.getBaseUrl(),
            Config.getRedirectUri(),
            Config.getClientId(),
            Config.getScope()
        )
    )

    override suspend fun authorize(): Either<AuthenticationError, AuthenticationFlow> {

        return Either.catch {
            authenticationCore.authorize()!!
        }.mapLeft {
            it.toAuthenticationError()
        }
    }

    override suspend fun authenticate(
        authenticatorType: AuthenticatorType,
        authenticatorParameters: AuthParams,
    ): Either<AuthenticationError, AuthenticationFlow> {
        return Either.catch {
            authenticationCore.authenticate(
                authenticatorType,
                authenticatorParameters
            )!!
        }.mapLeft {
            it.toAuthenticationError()
        }
    }

    override suspend fun getAccessToken(
        context: Context,
        authorizationCode: String
    ): Either<AuthenticationError, String> {
        return Either.catch {
            authenticationCore.getAccessToken(
                context,
                authorizationCode
            )!!
        }.mapLeft {
            it.toAuthenticationError()
        }
    }
}
