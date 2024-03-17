package io.wso2.android.api_authenticator.sdk.core.impl

import android.content.Context
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.di.AuthenticationCoreContainer
import io.wso2.android.api_authenticator.sdk.core.managers.app_auth.AppAuthManager
import io.wso2.android.api_authenticator.sdk.core.managers.authn.AuthnManager
import io.wso2.android.api_authenticator.sdk.core.managers.token.TokenManager
import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticationCoreException
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticationCoreException.Companion.AUTHORIZATION_SERVICE_NOT_INITIALIZED
import net.openid.appauth.TokenResponse
import java.io.IOException
import java.lang.ref.WeakReference

/**
 * Authentication core class which has the core functionality of the Authenticator SDK.
 *
 * @property authenticationCoreConfig Configuration of the [AuthenticationCore]. [AuthenticationCoreConfig]
 */
class AuthenticationCore private constructor(
    private val authenticationCoreConfig: AuthenticationCoreConfig
) : AuthenticationCoreDef {
    /**
     * Instance of the [AuthnManager] that will be used throughout the application
     */
    private var authnMangerInstance: AuthnManager =
        AuthenticationCoreContainer.getAuthMangerInstance(
            authenticationCoreConfig
        )

    /**
     * Instance of the [AppAuthManager] that will be used throughout the application
     */
    private var appAuthManagerInstance: AppAuthManager =
        AuthenticationCoreContainer.getAppAuthManagerInstance(
            authenticationCoreConfig
        )

    companion object {
        /**
         * Instance of the [AuthenticationCore] that will be used throughout the application
         */
        private var authenticationCoreInstance = WeakReference<AuthenticationCore?>(null)

        /**
         * Initialize the AuthenticationCore instance and return the instance.
         *
         * @param authenticationCoreConfig Configuration of the Authenticator [AuthenticationCoreConfig]
         *
         * @return Initialized [AuthenticationCore] instance
         */
        fun getInstance(
            authenticationCoreConfig: AuthenticationCoreConfig
        ): AuthenticationCore {
            var authenticationCore = authenticationCoreInstance.get()
            if (authenticationCore == null) {
                authenticationCore = AuthenticationCore(authenticationCoreConfig)
                authenticationCoreInstance = WeakReference(authenticationCore)
            }
            return authenticationCore
        }

        /**
         * Get the AuthenticationCore instance.
         * This method will return null if the AuthenticationCore instance is not initialized.
         *
         * @return [AuthenticationCore] instance
         *
         * @throws [AuthenticationCoreException] If the AuthenticationCore instance is not initialized
         */
        fun getInstance(): AuthenticationCore {
            return authenticationCoreInstance.get()
                ?: throw AuthenticationCoreException(
                    AUTHORIZATION_SERVICE_NOT_INITIALIZED
                )
        }
    }

    /**
     * Get the [TokenManager] instance.
     *
     * @param context The [Context] instance.
     *
     * @return [TokenManager] instance.
     */
    private fun getTokenManagerInstance(context: Context): TokenManager {
        return AuthenticationCoreContainer.getTokenManagerInstance(context)
    }

    /**
     * Authorize the application.
     * This method will call the authorization endpoint and get the authenticators available for the
     * first step in the authentication flow.
     *
     * @throws [AuthenticationCoreException] If the authorization fails
     * @throws [IOException] If the request fails due to a network error
     */
    override suspend fun authorize(): AuthenticationFlow? = authnMangerInstance.authorize()

    /**
     * Send the authentication parameters to the authentication endpoint and get the next step of the
     * authentication flow. If the authentication flow has only one step, this method will return
     * the success response of the authentication flow if the authentication is successful.
     *
     * @param authenticatorType Authenticator type of the selected authenticator
     * @param authenticatorParameters Authenticator parameters of the selected authenticator
     *
     * @throws [AuthenticationCoreException] If the authentication fails
     * @throws [IOException] If the request fails due to a network error
     *
     * @return [AuthenticationFlow] with the next step of the authentication flow
     */
    override suspend fun authenticate(
        authenticatorType: AuthenticatorType,
        authenticatorParameters: AuthParams,
    ): AuthenticationFlow? = authnMangerInstance.authenticate(
            authenticatorType,
            authenticatorParameters
        )

    /**
     * Exchange the authorization code for the access token.
     *
     * @param authorizationCode Authorization code
     * @param context Context of the application
     *
     * @throws [AppAuthManagerException] If the token request fails.
     *
     * @return Token response [TokenResponse]
     */
    override suspend fun exchangeAuthorizationCode(
        authorizationCode: String,
        context: Context,
    ): TokenResponse? = appAuthManagerInstance.exchangeAuthorizationCode(
        authorizationCode,
        context
    )

    /**
     * Perform the refresh token grant.
     *
     * @param refreshToken Refresh token
     * @param context Context of the application
     *
     * @throws [AppAuthManagerException] If the token request fails.
     *
     * @return Token response [TokenResponse]
     */
    override suspend fun performRefreshTokenGrant(
        refreshToken: String,
        context: Context,
    ): TokenResponse? = appAuthManagerInstance.performRefreshTokenGrant(refreshToken, context)

    /**
     * Save the tokens to the token data store.
     *
     * @param tokenResponse The [TokenResponse] instance.
     */
    override suspend fun saveTokens(context: Context, tokenResponse: TokenResponse): Unit? =
        getTokenManagerInstance(context).saveTokens(tokenResponse)

    /**
     * Get the access token from the token data store.
     *
     * @return The access token [String]
     */
    override suspend fun getAccessToken(context: Context): String? =
        getTokenManagerInstance(context).getAccessToken()

    /**
     * Get the refresh token from the token data store.
     *
     * @return The refresh token [String]
     */
    override suspend fun getRefreshToken(context: Context): String? =
        getTokenManagerInstance(context).getRefreshToken()

    /**
     * Get the ID token from the token data store.
     *
     * @return The ID token [String]
     */
    override suspend fun getIDToken(context: Context): String? =
        getTokenManagerInstance(context).getIDToken()

    /**
     * Get the access token expiration time from the token data store.
     *
     * @return The access token expiration time [Long]
     */
    override suspend fun getAccessTokenExpirationTime(context: Context): Long? =
        getTokenManagerInstance(context).getAccessTokenExpirationTime()

    /**
     * Get the scope from the token data store.
     *
     * @return The scope [String]
     */
    override suspend fun getScope(context: Context): String? =
        getTokenManagerInstance(context).getScope()

    /**
     * Get the token type from the token data store.
     *
     * @return The token type [String]
     */
    override suspend fun getTokenType(context: Context): String? =
        getTokenManagerInstance(context).getTokenType()

    /**
     * Clear the tokens from the token data store.
     */
    override suspend fun clearTokens(context: Context): Unit? =
        getTokenManagerInstance(context).clearTokens()

    /**
     * Validate the access token, by checking the expiration time of the access token, and
     * by checking if the access token is null or empty.
     * **Here we are not calling the introspection endpoint to validate the access token!**
     *
     * @return `true` if the access token is valid, `false` otherwise.
     */
    override suspend fun validateAccessToken(context: Context): Boolean? =
        getTokenManagerInstance(context).validateAccessToken()
}
