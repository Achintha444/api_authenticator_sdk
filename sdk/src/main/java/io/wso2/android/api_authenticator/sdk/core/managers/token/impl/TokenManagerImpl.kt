package io.wso2.android.api_authenticator.sdk.core.managers.token.impl

import android.content.Context
import io.wso2.android.api_authenticator.sdk.core.di.TokenManagerImplContainer
import io.wso2.android.api_authenticator.sdk.core.managers.token.TokenManager
import io.wso2.android.api_authenticator.sdk.data.token.TokenDataStore
import net.openid.appauth.TokenResponse

/**
 * Use to manage the tokens.
 *
 * @property context The [Context] instance.
 */
internal class TokenManagerImpl internal constructor(private val context: Context) : TokenManager {
    // Get the token data store
    private val tokenDataStore: TokenDataStore =
        TokenManagerImplContainer.getTokenDataStoreFactory().getTokenDataStore(context)

    /**
     * Save the tokens to the token data store.
     *
     * @param tokenResponse The [TokenResponse] instance.
     */
    override suspend fun saveTokens(tokenResponse: TokenResponse): Unit? =
        tokenDataStore.saveTokens(tokenResponse)

    /**
     * Get the access token from the token data store.
     *
     * @return The access token [String]
     */
    override suspend fun getAccessToken(): String? =
        tokenDataStore.getAccessToken()

    /**
     * Get the refresh token from the token data store.
     *
     * @return The refresh token [String]
     */
    override suspend fun getRefreshToken(): String? =
        tokenDataStore.getRefreshToken()

    /**
     * Get the ID token from the token data store.
     *
     * @return The ID token [String]
     */
    override suspend fun getIDToken(): String? =
        tokenDataStore.getIDToken()

    /**
     * Get the access token expiration time from the token data store.
     *
     * @return The access token expiration time [Long]
     */
    override suspend fun getAccessTokenExpirationTime(): Long? =
        tokenDataStore.getAccessTokenExpirationTime()

    /**
     * Get the scope from the token data store.
     *
     * @return The scope [String]
     */
    override suspend fun getScope(): String? =
        tokenDataStore.getScope()

    /**
     * Get the token type from the token data store.
     *
     * @return The token type [String]
     */
    override suspend fun getTokenType(): String? =
        tokenDataStore.getTokenType()

    /**
     * Clear the tokens from the token data store.*
     */
    override suspend fun clearTokens(): Unit? =
        tokenDataStore.clearTokens()

    /**
     * Validate the access token, by checking the expiration time of the access token, and
     * by checking if the access token is null or empty.
     * **Here we are not calling the introspection endpoint to validate the access token!**
     *
     * @return `true` if the access token is valid, `false` otherwise.
     */
    override suspend fun validateAccessToken(): Boolean? {
        /**
         * Get the token response from the token data store. If the token response is null, then the
         * access token is not valid, and exception will be thrown when accessing the access token.
         */
        val tokenResponse: TokenResponse? = tokenDataStore.getTokenResponse()
        val accessToken: String? = tokenResponse!!.accessToken

        // If the access token is null or empty, then it is not valid
        if (accessToken.isNullOrBlank()) {
            return false
        }

        // If the access token expiration time is null, then it is not valid
        return tokenResponse!!.accessTokenExpirationTime?.let { expirationTime ->
            expirationTime > System.currentTimeMillis()
        } ?: false
    }
}
