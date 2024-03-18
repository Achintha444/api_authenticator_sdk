package io.wso2.android.api_authenticator.sdk.core.managers.app_auth

import android.content.Context
import io.wso2.android.api_authenticator.sdk.models.exceptions.AppAuthManagerException
import io.wso2.android.api_authenticator.sdk.models.state.TokenState
import net.openid.appauth.AuthState
import net.openid.appauth.TokenResponse

/**
 * Interface which has the methods to manage the AppAuth SDK.
 */
interface AppAuthManager {
    /**
     * Use to exchange the authorization code for the access token.
     *
     * @param authorizationCode The authorization code.
     *
     * @throws AppAuthManagerException If the token request fails.
     *
     * @return The [TokenState] instance.
     */
    suspend fun exchangeAuthorizationCode(
        authorizationCode: String,
        context: Context
    ): TokenState?

    /**
     * Use to perform the refresh token grant.
     *
     * @param refreshToken The refresh token.
     * @param context The [Context] instance.
     *
     * @throws AppAuthManagerException If the token request fails.
     *
     * @return The [TokenState] instance.
     */
    suspend fun performRefreshTokenGrant(
        refreshToken: String?,
        context: Context
    ): TokenState?

    /**
     * Perform an action with fresh tokens.
     *
     * @param context The [Context] instance.
     * @param action The action to perform.
     *
     * @return The [TokenState] instance.
     */
    suspend fun performActionWithFreshTokens(
        context: Context,
        action: suspend (String, String) -> Unit
    ): TokenState?
}
