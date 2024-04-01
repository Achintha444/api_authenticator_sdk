package io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.google_native

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams

/**
 * Interface to be implemented by the Google Native Authentication Handler Manager
 * This manager is responsible for handling the Google Native Authentication using the
 * Credential Manager API
 */
interface GoogleNativeAuthenticationHandlerManager {
    /**
     * Authenticate the user with Google using the Credential Manager API
     *
     * @param context [Context] of the application
     *
     * @return Google ID Token of the authenticated user
     */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun authenticateWithGoogleNative(context: Context): AuthParams?

    /**
     * Logout the user from the google account
     *
     * @param context [Context] of the application
     */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun logout(context: Context)
}
