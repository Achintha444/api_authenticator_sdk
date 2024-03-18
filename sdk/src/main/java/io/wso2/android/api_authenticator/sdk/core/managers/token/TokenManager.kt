package io.wso2.android.api_authenticator.sdk.core.managers.token

import net.openid.appauth.AuthState
import net.openid.appauth.TokenResponse

/**
 * Interface which has the methods to manage the tokens.
 */
interface TokenManager {

    /**
     * Save the [AuthState] to the data store.
     *
     * @param appAuthState The [AuthState] instance.
     */
     suspend fun saveAppAuthState(appAuthState: AuthState): Unit?

    /**
     * Get the [AuthState] from the data store.
     *
     * @return The [AuthState] instance.
     */
     suspend fun getAppAuthState(): AuthState?

    /**
     * Get the access token from the token data store.
     *
     * @return The access token [String]
     */
    suspend fun getAccessToken(): String?

    /**
     * Get the refresh token from the token data store.
     *
     * @return The refresh token [String]
     */
    suspend fun getRefreshToken(): String?

    /**
     * Get the ID token from the token data store.
     *
     * @return The ID token [String]
     */
    suspend fun getIDToken(): String?

    /**
     * Get the access token expiration time from the token data store.
     *
     * @return The access token expiration time [Long]
     */
    suspend fun getAccessTokenExpirationTime(): Long?

    /**
     * Get the scope from the token data store.
     *
     * @return The scope [String]
     */
    suspend fun getScope(): String?

    /**
     * Clear the tokens from the token data store.
     */
    suspend fun clearTokens(): Unit?

    /**
     * Validate the access token, by checking the expiration time of the access token, and
     * by checking if the access token is null or empty.
     * **Here we are not calling the introspection endpoint to validate the access token!**
     *
     * @return `true` if the access token is valid, `false` otherwise.
     */
    suspend fun validateAccessToken(): Boolean?
}
