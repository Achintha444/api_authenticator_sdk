package io.wso2.android.api_authenticator.sdk.provider.providers.token

import android.content.Context
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.provider.di.TokenProviderContainer
import io.wso2.android.api_authenticator.sdk.provider.di.TokenProviderManagerImplContainer
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.token.TokenProviderManager
import java.lang.ref.WeakReference

/**
 * The [TokenProvider] class provides the functionality to get the tokens, validate the tokens,
 * refresh the tokens, and clear the tokens.
 */
class TokenProvider {
    /**
     * Instance of the [AuthenticationCoreDef] that will be used throughout the application
     */
    private val authenticationCore: AuthenticationCoreDef? by lazy {
        TokenProviderContainer.getAuthenticationCoreDef()
    }

    private val tokenProviderManager: TokenProviderManager by lazy {
        TokenProviderContainer.getTokenProviderManager(authenticationCore!!)
    }

    companion object {
        /**
         * Instance of the [TokenProvider] that will be used throughout the application
         */
        private var tokenProviderInstance: WeakReference<TokenProvider> =
            WeakReference(null)

        /**
         * Initialize the [TokenProvider] instance and return the instance.
         */
        fun getInstance(): TokenProvider {
            var tokenProvider = tokenProviderInstance.get()
            if (tokenProvider == null) {
                tokenProvider = TokenProvider()
                tokenProviderInstance = WeakReference(tokenProvider)
            }
            return tokenProvider
        }
    }

    /**
     * Get the access token from the token.
     *
     * @param context The [Context] instance.
     *
     * @return The access token [String]
     */
    suspend fun getAccessToken(context: Context): String? =
        tokenProviderManager.getAccessToken(context)

    /**
     * Get the refresh token from the token.
     *
     * @param context The [Context] instance.
     *
     * @return The refresh token [String]
     */
    suspend fun getRefreshToken(context: Context): String? =
        tokenProviderManager.getRefreshToken(context)

    /**
     * Get the ID token from the token.
     *
     * @param context The [Context] instance.
     *
     * @return The ID token [String]
     */
    suspend fun getIDToken(context: Context): String? = tokenProviderManager.getIDToken(context)

    /**
     * Get the access token expiration time from the token.
     *
     * @param context The [Context] instance.
     *
     * @return The access token expiration time [Long]
     */
    suspend fun getAccessTokenExpirationTime(context: Context): Long? =
        tokenProviderManager.getAccessTokenExpirationTime(context)

    /**
     * Get the scope from the token.
     *
     * @param context The [Context] instance.
     *
     * @return The scope [String]
     */
    suspend fun getScope(context: Context): String? = tokenProviderManager.getScope(context)


    /**
     * Validate the access token, by checking the expiration time of the access token, and
     * by checking if the access token is null or empty.
     * **Here we are not calling the introspection endpoint to validate the access token!**
     *
     * @return `true` if the access token is valid, `false` otherwise.
     */
    suspend fun validateAccessToken(context: Context): Boolean? =
        tokenProviderManager.validateAccessToken(context)

    /**
     * Perform refresh token grant. This method will perform the refresh token grant and save the
     * updated token state in the data store. If refresh token grant fails, it will throw an
     * Exception.
     *
     * @param context The [Context] instance.
     */
    suspend fun performRefreshTokenGrant(context: Context) =
        tokenProviderManager.performRefreshTokenGrant(context)

    /**
     * Perform an action with fresh tokens. This method will perform the action with fresh tokens
     * and save the updated token state in the data store. Developer can directly use this method
     * perform an action with fresh tokens, without worrying about refreshing the tokens. If this
     * action fails, it will throw an Exception.
     *
     * @param context The [Context] instance.
     * @param action The action to perform.
     */
    suspend fun performActionWithFreshTokens(
        context: Context,
        action: suspend (String?, String?) -> Unit
    ) = tokenProviderManager.performActionWithFreshTokens(context, action)

    /**
     * Clear the tokens from the token data store. This method will clear the tokens from the
     * data store. After calling this method, developer needs to perform the authorization flow
     * again to get the tokens.
     *
     * @param context The [Context] instance.
     */
    suspend fun clearTokens(context: Context) = tokenProviderManager.clearTokens(context)
}
