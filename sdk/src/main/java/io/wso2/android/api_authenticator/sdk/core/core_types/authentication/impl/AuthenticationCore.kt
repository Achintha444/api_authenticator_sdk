package io.wso2.android.api_authenticator.sdk.core.core_types.authentication.impl

import android.content.Context
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.core_types.authentication.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.di.AuthenticationCoreContainer
import io.wso2.android.api_authenticator.sdk.core.managers.app_auth.AppAuthManager
import io.wso2.android.api_authenticator.sdk.core.managers.authenticator.AuthenticatorManager
import io.wso2.android.api_authenticator.sdk.core.managers.authn.AuthnManager
import io.wso2.android.api_authenticator.sdk.core.managers.flow.FlowManager
import io.wso2.android.api_authenticator.sdk.core.managers.logout.LogoutManager
import io.wso2.android.api_authenticator.sdk.core.managers.token.TokenManager
import io.wso2.android.api_authenticator.sdk.core.managers.user.UserManager
import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticationCoreException
import io.wso2.android.api_authenticator.sdk.models.exceptions.AppAuthManagerException
import io.wso2.android.api_authenticator.sdk.models.exceptions.LogoutException
import io.wso2.android.api_authenticator.sdk.models.state.TokenState
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
     * Instance of the [AuthenticatorManager] that will be used throughout the application
     */
    private val authenticatorManager: AuthenticatorManager by lazy {
        AuthenticationCoreContainer.getAuthenticatorManagerInstance(authenticationCoreConfig)
    }

    /**
     * Instance of the [FlowManager] that will be used throughout the application
     */
    private val flowManager: FlowManager by lazy {
        AuthenticationCoreContainer.getFlowManagerInstance()
    }

    /**
     * Instance of the [AuthnManager] that will be used throughout the application
     */
    private val authnMangerInstance: AuthnManager by lazy {
        AuthenticationCoreContainer.getAuthMangerInstance(authenticationCoreConfig)
    }

    /**
     * Instance of the [AppAuthManager] that will be used throughout the application
     */
    private val appAuthManagerInstance: AppAuthManager by lazy {
        AuthenticationCoreContainer.getAppAuthManagerInstance(authenticationCoreConfig)
    }

    /**
     * Instance of the [UserManager] that will be used throughout the application
     */
    private val userManagerInstance: UserManager by lazy {
        AuthenticationCoreContainer.getUserManagerInstance(authenticationCoreConfig)
    }

    /**
     * Instance of the [LogoutManager] that will be used throughout the application
     */
    private val logoutManagerInstance: LogoutManager by lazy {
        AuthenticationCoreContainer.getLogoutManagerInstance(authenticationCoreConfig)
    }

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
        fun getInstance(authenticationCoreConfig: AuthenticationCoreConfig): AuthenticationCore {
            var authenticationCore = authenticationCoreInstance.get()
            if (authenticationCore == null ||
                authenticationCore.authenticationCoreConfig != authenticationCoreConfig) {
                authenticationCore = AuthenticationCore(authenticationCoreConfig)
                authenticationCoreInstance = WeakReference(authenticationCore)
            }
            return authenticationCore
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
     * @param authenticatorParameters Authenticator parameters of the selected authenticator as a
     * [LinkedHashMap] with the key as the parameter name and the value as the parameter value
     *
     * @throws [AuthenticationCoreException] If the authentication fails
     * @throws [IOException] If the request fails due to a network error
     *
     * @return [AuthenticationFlow] with the next step of the authentication flow
     */
    override suspend fun authenticate(
        authenticatorType: AuthenticatorType,
        authenticatorParameters: LinkedHashMap<String, String>
    ): AuthenticationFlow? = authnMangerInstance.authenticate(
        authenticatorType,
        authenticatorParameters
    )

    /**
     * Get the authenticator details of the given authenticator type.
     * This should call before authenticating with the any authenticator.
     *
     * @param authenticatorType Authenticator type
     *
     * @return Authenticator details [AuthParams]
     */
    override suspend fun getDetailsOfAuthenticatorType(
        authenticatorType: AuthenticatorType
    ): AuthenticatorType {
        val flowId: String = flowManager.getFlowId()

        return authenticatorManager.getDetailsOfAuthenticatorType(flowId, authenticatorType)
    }

    /**
     * Exchange the authorization code for the access token.
     *
     * @param authorizationCode Authorization code
     * @param context Context of the application
     *
     * @throws [AppAuthManagerException] If the token request fails.
     *
     * @return token state [TokenState]
     */
    override suspend fun exchangeAuthorizationCode(
        authorizationCode: String,
        context: Context,
    ): TokenState? = appAuthManagerInstance.exchangeAuthorizationCode(
        authorizationCode,
        context
    )

    /**
     * Perform the refresh token grant.
     *
     * @param context Context of the application
     * @param tokenState The [TokenState] instance.
     *
     * @throws [AppAuthManagerException] If the token request fails.
     *
     * @return Updated [TokenState] instance.
     */
    override suspend fun performRefreshTokenGrant(
        context: Context,
        tokenState: TokenState,
    ): TokenState? = appAuthManagerInstance.performRefreshTokenGrant(context, tokenState)

    /**
     * Perform an action with the tokens. If the token is expired, it will perform the refresh the
     * tokens, and then perform the action. This will also update the token in the data store
     * as well.
     *
     * @param context The [Context] instance.
     * @param tokenState The [TokenState] instance.
     * @param action The action to perform.
     *
     * @return Updated [TokenState] instance.
     */
    override suspend fun performAction(
        context: Context,
        tokenState: TokenState,
        action: suspend (String?, String?) -> Unit
    ): TokenState? =
        appAuthManagerInstance.performAction(context, tokenState, action)

    /**
     * Save the [TokenState] to the data store.
     *
     * @param context Context of the application
     * @param tokenState The [TokenState] instance.
     */
    override suspend fun saveTokenState(context: Context, tokenState: TokenState): Unit? =
        getTokenManagerInstance(context).saveTokenState(tokenState)

    /**
     * Get the [TokenState] from the data store.
     *
     * @param context Context of the application
     */
    override suspend fun getTokenState(context: Context): TokenState? =
        getTokenManagerInstance(context).getTokenState()

    /**
     * Get the access token from the token data store.
     *
     * @param context Context of the application
     *
     * @return The access token [String]
     */
    override suspend fun getAccessToken(context: Context): String? =
        getTokenManagerInstance(context).getAccessToken()

    /**
     * Get the refresh token from the token data store.
     *
     * @param context Context of the application
     *
     * @return The refresh token [String]
     */
    override suspend fun getRefreshToken(context: Context): String? =
        getTokenManagerInstance(context).getRefreshToken()

    /**
     * Get the ID token from the token data store.
     *
     * @param context Context of the application
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
     * @param context Context of the application
     *
     * @return The scope [String]
     */
    override suspend fun getScope(context: Context): String? =
        getTokenManagerInstance(context).getScope()

    /**
     * Clear the tokens from the token data store.
     *
     * @param context Context of the application
     */
    override suspend fun clearTokens(context: Context): Unit? =
        getTokenManagerInstance(context).clearTokens()

    /**
     * Validate the access token, by checking the expiration time of the access token, and
     * by checking if the access token is null or empty.
     * **Here we are not calling the introspection endpoint to validate the access token!**
     *
     * @param context Context of the application
     *
     * @return `true` if the access token is valid, `false` otherwise.
     */
    override suspend fun validateAccessToken(context: Context): Boolean? =
        getTokenManagerInstance(context).validateAccessToken()

    /**
     * Get the user details from the Identity Server.
     *
     * @param accessToken Access token to authorize the request
     *
     * @return User details as a [LinkedHashMap]
     */
    override suspend fun getUserDetails(accessToken: String?): LinkedHashMap<String, Any>? =
        userManagerInstance.getUserDetails(accessToken)

    /**
     * Logout the user from the application.
     *
     * @param idToken Id token of the user
     *
     * @throws [LogoutException] If the logout fails
     * @throws [IOException] If the request fails due to a network error
     */
    override suspend fun logout(idToken: String) {
        logoutManagerInstance.logout(idToken)
    }
}
