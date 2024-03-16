package io.wso2.android.api_authenticator.sdk.core.managers.token.impl

import android.content.Context
import io.wso2.android.api_authenticator.sdk.core.di.TokenManagerImplContainer
import io.wso2.android.api_authenticator.sdk.core.managers.token.TokenManager
import io.wso2.android.api_authenticator.sdk.data.token.TokenDataStore
import kotlinx.coroutines.runBlocking
import net.openid.appauth.TokenResponse
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Use to manage the tokens.
 *
 * @property context The [Context] instance.
 */
internal class TokenManagerImpl(
    private val context: Context
) : TokenManager {
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

//    suspend fun validateAccessToken(): Boolean? = suspendCoroutine { continuation ->
//        // Validate access token based on the access token time
//        runBlocking {
//            runCatching {
//                getAccessTokenExpirationTime()
//            }.onSuccess {
//
//            }.onFailure {
//                continuation.resumeWithException(it)
//            }
//        }
//    }
}
