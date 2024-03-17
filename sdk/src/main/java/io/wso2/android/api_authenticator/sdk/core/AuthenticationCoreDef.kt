package io.wso2.android.api_authenticator.sdk.core

import android.content.Context
import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow
import net.openid.appauth.TokenResponse

/**
 * Authentication core class interface which has the core functionality of the Authenticator SDK.
 */
interface AuthenticationCoreDef {
    /**
     * Authorize the application.
     * This method will call the authorization endpoint and get the authenticators available for the
     * first step in the authentication flow.
     */
    suspend fun authorize(): io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow?

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
        authenticatorType: io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType,
        authenticatorParameters: io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams,
    ): io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow?

    /**
     * Exchange the authorization code for the access token.
     *
     * @param authorizationCode Authorization code
     * @param context Context of the application
     *
     * @return Token response [TokenResponse]
     */
    suspend fun exchangeAuthorizationCode(
        authorizationCode: String,
        context: Context,
    ): TokenResponse?

    /**
     * Perform the refresh token grant.
     *
     * @param refreshToken Refresh token
     * @param context Context of the application
     *
     * @return Token response [TokenResponse]
     */
    suspend fun performRefreshTokenGrant(
        refreshToken: String,
        context: Context,
    ): TokenResponse?

    /**
     * Save the tokens to the token data store.
     *
     * @param tokenResponse The [TokenResponse] instance.
     */
    suspend fun saveTokens(context: Context, tokenResponse: TokenResponse): Unit?

    /**
     * Get the access token from the token data store.
     *
     * @return The access token [String]
     */
    suspend fun getAccessToken(context: Context): String?

    /**
     * Get the refresh token from the token data store.
     *
     * @return The refresh token [String]
     */
    suspend fun getRefreshToken(context: Context): String?

    /**
     * Get the ID token from the token data store.
     *
     * @return The ID token [String]
     */
    suspend fun getIDToken(context: Context): String?

    /**
     * Get the access token expiration time from the token data store.
     *
     * @return The access token expiration time [Long]
     */
    suspend fun getAccessTokenExpirationTime(context: Context): Long?

    /**
     * Get the scope from the token data store.
     *
     * @return The scope [String]
     */
    suspend fun getScope(context: Context): String?

    /**
     * Get the token type from the token data store.
     *
     * @return The token type [String]
     */
    suspend fun getTokenType(context: Context): String?

    /**
     * Clear the tokens from the token data store.
     */
    suspend fun clearTokens(context: Context): Unit?

    /**
     * Validate the access token, by checking the expiration time of the access token, and
     * by checking if the access token is null or empty.
     * **Here we are not calling the introspection endpoint to validate the access token!**
     *
     * @return `true` if the access token is valid, `false` otherwise.
     */
    suspend fun validateAccessToken(context: Context): Boolean?
}
