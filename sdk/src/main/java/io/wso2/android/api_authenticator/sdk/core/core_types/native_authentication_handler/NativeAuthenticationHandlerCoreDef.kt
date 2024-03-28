package io.wso2.android.api_authenticator.sdk.core.core_types.native_authentication_handler

import android.content.Context

/**
 * Native Authentication Handler Core class interface which has the core functionality to
 * handle the Native Authentication.
 */
interface NativeAuthenticationHandlerCoreDef {
    /**
     * Handle the Google Native Authentication.
     * This method will authenticate the user with the Google Native Authentication.
     *
     * @param context Context of the application
     *
     * @return idToken sent by the Google Native Authentication
     */
    suspend fun handleGoogleNativeAuthentication(context: Context): String?
}
