package io.wso2.android.api_authenticator.sdk.data.token

import io.wso2.android.api_authenticator.sdk.models.state.TokenState
import net.openid.appauth.AuthState

/**
 * Interface which has the methods to manage the token data store.
 */
interface TokenDataStore {

    /**
     * Save the [TokenState] to the data store.
     *
     * @param tokenState The [TokenState] instance.
     */
    suspend fun saveTokenState(tokenState: TokenState): Unit

    /**
     * Get the [TokenState] from the data store.
     *
     * @return The [TokenState] instance.
     */
    suspend fun getTokenState(): TokenState?

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
    suspend fun clearTokens()
}
