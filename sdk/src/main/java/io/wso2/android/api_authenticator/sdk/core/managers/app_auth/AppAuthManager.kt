package io.wso2.android.api_authenticator.sdk.core.managers.app_auth

import android.content.Context
import io.wso2.android.api_authenticator.sdk.models.exceptions.AppAuthManagerException
import io.wso2.android.api_authenticator.sdk.models.state.TokenState

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
     * @param context The [Context] instance.
     * @param tokenState The [TokenState] instance.
     *
     * @throws AppAuthManagerException If the token request fails.
     *
     * @return Updated [TokenState] instance.
     */
    suspend fun performRefreshTokenGrant(
        context: Context,
        tokenState: TokenState,
    ): TokenState?

    /**
     * Perform an action with fresh tokens.
     *
     * @param context The [Context] instance.
     * @param tokenState The [TokenState] instance.
     * @param action The action to perform.
     *
     * @return Updated [TokenState] instance.
     */
    suspend fun performActionWithFreshTokens(
        context: Context,
        tokenState: TokenState,
        action: suspend (String?, String?) -> Unit
    ): TokenState?
}
