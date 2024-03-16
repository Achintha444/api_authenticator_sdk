package io.wso2.android.api_authenticator.sdk.core.managers.token

import net.openid.appauth.TokenResponse

/**
 * Interface which has the methods to manage the tokens.
 */
interface TokenManager {
    /**
     * Save the tokens to the token data store.
     *
     * @param tokenResponse The [TokenResponse] instance.
     */
    suspend fun saveTokens(tokenResponse: TokenResponse): Unit?

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
     * Get the token type from the token data store.
     *
     * @return The token type [String]
     */
    suspend fun getTokenType(): String?

    /**
     * Clear the tokens from the token data store.
     */
    suspend fun clearTokens(): Unit?
}
