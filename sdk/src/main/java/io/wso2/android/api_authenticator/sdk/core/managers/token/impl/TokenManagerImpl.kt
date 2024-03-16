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
    context: Context
) : TokenManager {
    private val tokenDataStore: TokenDataStore =
        TokenManagerImplContainer.getTokenDataStoreFactory().getTokenDataStore(context)

    /**
     * Save the tokens to the token data store.
     *
     * @param tokenResponse The [TokenResponse] instance.
     */
    override suspend fun saveTokens(tokenResponse: TokenResponse): Unit? =
        suspendCoroutine { continuation ->
            runBlocking {
                runCatching {
                    tokenDataStore.saveTokens(tokenResponse)
                }.onSuccess {
                    continuation.resume(Unit)
                }.onFailure {
                    continuation.resumeWithException(it)
                }
            }
        }

    /**
     * Get the access token from the token data store.
     *
     * @return The access token [String]
     */
    override suspend fun getAccessToken(): String? =
        suspendCoroutine { continuation ->
            runBlocking {
                runCatching {
                    tokenDataStore.getAccessToken()
                }.onSuccess {
                    continuation.resume(it)
                }.onFailure {
                    continuation.resumeWithException(it)
                }
            }
        }

    /**
     * Get the refresh token from the token data store.
     *
     * @return The refresh token [String]
     */
    override suspend fun getRefreshToken(): String? =
        suspendCoroutine { continuation ->
            runBlocking {
                runCatching {
                    tokenDataStore.getRefreshToken()
                }.onSuccess {
                    continuation.resume(it)
                }.onFailure {
                    continuation.resumeWithException(it)
                }
            }
        }

    /**
     * Get the ID token from the token data store.
     *
     * @return The ID token [String]
     */
    override suspend fun getIDToken(): String? =
        suspendCoroutine { continuation ->
            runBlocking {
                runCatching {
                    tokenDataStore.getIDToken()
                }.onSuccess {
                    continuation.resume(it)
                }.onFailure {
                    continuation.resumeWithException(it)
                }
            }
        }

    /**
     * Get the access token expiration time from the token data store.
     *
     * @return The access token expiration time [Long]
     */
    override suspend fun getAccessTokenExpirationTime(): Long? =
        suspendCoroutine { continuation ->
            runBlocking {
                runCatching {
                    tokenDataStore.getAccessTokenExpirationTime()
                }.onSuccess {
                    continuation.resume(it)
                }.onFailure {
                    continuation.resumeWithException(it)
                }
            }
        }

    /**
     * Get the scope from the token data store.
     *
     * @return The scope [String]
     */
    override suspend fun getScope(): String? =
        suspendCoroutine { continuation ->
            runBlocking {
                runCatching {
                    tokenDataStore.getScope()
                }.onSuccess {
                    continuation.resume(it)
                }.onFailure {
                    continuation.resumeWithException(it)
                }
            }
        }

    /**
     * Get the token type from the token data store.
     *
     * @return The token type [String]
     */
    override suspend fun getTokenType(): String? =
        suspendCoroutine { continuation ->
            runBlocking {
                runCatching {
                    tokenDataStore.getTokenType()
                }.onSuccess {
                    continuation.resume(it)
                }.onFailure {
                    continuation.resumeWithException(it)
                }
            }
        }

    /**
     * Clear the tokens from the token data store.*
     */
    override suspend fun clearTokens(): Unit? =
        suspendCoroutine { continuation ->
            runBlocking {
                runCatching {
                    tokenDataStore.clearTokens()
                }.onSuccess {
                    continuation.resume(Unit)
                }.onFailure {
                    continuation.resumeWithException(it)
                }
            }
        }

    suspend fun validateAccessToken(): Boolean? = suspendCoroutine { continuation ->
        // Validate access token based on the access token time
        runBlocking {
            runCatching {
                getAccessTokenExpirationTime()
            }.onSuccess {

            }.onFailure {
                continuation.resumeWithException(it)
            }
        }
    }
}
