package io.wso2.android.api_authenticator.sdk.core.managers.app_auth

import android.content.Context
import io.wso2.android.api_authenticator.sdk.models.exceptions.AppAuthManagerException

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
     * @return The access token [String]
     */
    suspend fun getAccessToken(
        authorizationCode: String,
        context: Context
    ): String?
}
