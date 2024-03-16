package io.wso2.android.api_authenticator.sdk.core.managers.app_auth

import android.content.Context
import io.wso2.android.api_authenticator.sdk.models.exceptions.AppAuthManagerException
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
     * @return The [TokenResponse] instance.
     */
    suspend fun exchangeAuthorizationCode(
        authorizationCode: String,
        context: Context
    ): TokenResponse?

    /**
     * Use to perform the refresh token grant.
     *
     * @param refreshToken The refresh token.
     * @param context The [Context] instance.
     *
     * @throws AppAuthManagerException If the token request fails.
     *
     * @return The [TokenResponse] instance.
     */
    suspend fun performRefreshTokenGrant(
        refreshToken: String?,
        context: Context
    ): TokenResponse?
}
