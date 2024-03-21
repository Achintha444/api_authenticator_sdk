package io.wso2.android.api_authenticator.sdk.sample.domain.repository

import io.wso2.android.api_authenticator.sdk.sample.domain.model.error.AuthenticationError
import android.content.Context
import arrow.core.Either
import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow

/**
 * Use as a repository to handle the authentication related operations using Core package of the SDK
 */
interface AuthenticationRepository {
    suspend fun authorize(): Either<AuthenticationError, AuthenticationFlow>

    suspend fun authenticate(
        authenticatorType: AuthenticatorType,
        authenticatorParameters: LinkedHashMap<String, String>
    ): Either<AuthenticationError, AuthenticationFlow>

    suspend fun getAccessToken(
        context: Context,
        authorizationCode: String
    ): Either<AuthenticationError, String>
}
