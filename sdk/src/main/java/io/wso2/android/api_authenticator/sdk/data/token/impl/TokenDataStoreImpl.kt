package io.wso2.android.api_authenticator.sdk.data.token.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.wso2.android.api_authenticator.sdk.data.token.TokenDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import net.openid.appauth.TokenResponse

private const val DATA_STORE_NAME = "token_data_store"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = DATA_STORE_NAME
)

/**
 * [TokenDataStoreImpl] is the implementation of the [TokenDataStore]
 *
 * @property context The [Context] instance.
 */
class TokenDataStoreImpl(private val context: Context) : TokenDataStore {
    companion object {
        private val TOKEN_RESPONSE_TOKEN_KEY = stringPreferencesKey("token_response")
    }

    /**
     * Get the token response from the token data store.
     *
     * @return The [TokenResponse] instance.
     */
    override suspend fun getTokenResponse(): TokenResponse? = withContext(Dispatchers.IO) {
        val preferences: Preferences? = context.dataStore.data.firstOrNull()

        return@withContext preferences?.get(TOKEN_RESPONSE_TOKEN_KEY)?.let {
            TokenResponse.jsonDeserialize(it)
        }
    }

    /**
     * Save the tokens to the token data store.
     *
     * @param tokenResponse The [TokenResponse] instance.
     */
    override suspend fun saveTokens(tokenResponse: TokenResponse): Unit =
        withContext(Dispatchers.IO) {
            context.dataStore.edit { preferences ->
                preferences[TOKEN_RESPONSE_TOKEN_KEY] = tokenResponse.jsonSerializeString()
            }
        }

    /**
     * Get the access token from the token data store.
     *
     * @return The access token [String]
     */
    override suspend fun getAccessToken(): String? =
        getTokenResponse()?.accessToken

    /**
     * Get the refresh token from the token data store.
     *
     * @return The refresh token [String]
     */
    override suspend fun getRefreshToken(): String? =
        getTokenResponse()?.refreshToken

    /**
     * Get the ID token from the token data store.
     *
     * @return The ID token [String]
     */
    override suspend fun getIDToken(): String? =
        getTokenResponse()?.idToken

    /**
     * Get the access token expiration time from the token data store.
     *
     * @return The access token expiration time [Long]
     */
    override suspend fun getAccessTokenExpirationTime(): Long? =
        getTokenResponse()?.accessTokenExpirationTime

    /**
     * Get the scope from the token data store.
     *
     * @return The scope [String]
     */
    override suspend fun getScope(): String? =
        getTokenResponse()?.scope

    /**
     * Get the token type from the token data store.
     *
     * @return The token type [String]
     */
    override suspend fun getTokenType(): String? =
        getTokenResponse()?.tokenType

    /**
     * Clear the tokens from the token data store.
     */
    override suspend fun clearTokens(): Unit = withContext(Dispatchers.IO) {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
