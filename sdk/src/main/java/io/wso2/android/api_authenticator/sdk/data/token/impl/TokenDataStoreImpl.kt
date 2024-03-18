package io.wso2.android.api_authenticator.sdk.data.token.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.wso2.android.api_authenticator.sdk.data.token.TokenDataStore
import io.wso2.android.api_authenticator.sdk.models.state.TokenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import net.openid.appauth.AuthState
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
        private val TOKEN_STATE_KEY = stringPreferencesKey("TOKEN_STATE")
    }

    /**
     * Save the [TokenState] to the data store.
     *
     * @param tokenState The [TokenState] instance.
     */
    override suspend fun saveTokenState(tokenState: TokenState): Unit =
        withContext(Dispatchers.IO) {
            context.dataStore.edit { preferences ->
                preferences[TOKEN_STATE_KEY] = tokenState.toJsonString()
            }
        }

    /**
     * Get the [TokenState] from the data store.
     *
     * @return The [TokenState] instance.
     */
    override suspend fun getTokenState(): TokenState? = withContext(Dispatchers.IO) {
        val preferences: Preferences? = context.dataStore.data.firstOrNull()

        return@withContext preferences?.get(TOKEN_STATE_KEY)?.let {
            TokenState.fromJsonString(it)
        }
    }

    /**
     * Get the access token from the token data store.
     *
     * @return The access token [String]
     */
    override suspend fun getAccessToken(): String? =
        getTokenState()?.getAppAuthState()?.accessToken

    /**
     * Get the refresh token from the token data store.
     *
     * @return The refresh token [String]
     */
    override suspend fun getRefreshToken(): String? =
        getTokenState()?.getAppAuthState()?.refreshToken

    /**
     * Get the ID token from the token data store.
     *
     * @return The ID token [String]
     */
    override suspend fun getIDToken(): String? =
        getTokenState()?.getAppAuthState()?.idToken

    /**
     * Get the access token expiration time from the token data store.
     *
     * @return The access token expiration time [Long]
     */
    override suspend fun getAccessTokenExpirationTime(): Long? =
        getTokenState()?.getAppAuthState()?.accessTokenExpirationTime

    /**
     * Get the scope from the token data store.
     *
     * @return The scope [String]
     */
    override suspend fun getScope(): String? =
        getTokenState()?.getAppAuthState()?.scope

    /**
     * Clear the tokens from the token data store.
     */
    override suspend fun clearTokens(): Unit = withContext(Dispatchers.IO) {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
