package io.wso2.android.api_authenticator.sdk.core

import android.content.Context
import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow

/**
 * Authentication core class interface which has the core functionality of the Authenticator SDK.
 */
interface AuthenticationCoreDef {
    /**
     * Authorize the application.
     * This method will call the authorization endpoint and get the authenticators available for the
     * first step in the authentication flow.
     */
    suspend fun authorize(): AuthenticationFlow?

    /**
     * Send the authentication parameters to the authentication endpoint and get the next step of the
     * authentication flow. If the authentication flow has only one step, this method will return
     * the success response of the authentication flow if the authentication is successful.
     *
     * @param authenticatorType Authenticator type of the selected authenticator
     * @param authenticatorParameters Authenticator parameters of the selected authenticator
     *
     * @return [AuthenticationFlow] with the next step of the authentication flow
     */
    suspend fun authenticate(
        authenticatorType: AuthenticatorType,
        authenticatorParameters: AuthParams,
    ): AuthenticationFlow?

    /**
     * Get the access token using the authorization code.
     *
     * @param context Context of the application
     * @param authorizationCode Authorization code
     *
     * @return Access token [String]
     */
    suspend fun getAccessToken(
        context: Context,
        authorizationCode: String
    ): String?
}
