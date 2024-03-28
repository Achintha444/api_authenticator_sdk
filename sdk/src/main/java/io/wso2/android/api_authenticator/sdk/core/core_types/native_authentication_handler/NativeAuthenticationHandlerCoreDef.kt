package io.wso2.android.api_authenticator.sdk.core.core_types.native_authentication_handler

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher

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

    /**
     * Handle the Google Native Authentication result using the legacy one tap method.
     * This method will authenticate the user with the Google Native Authentication using the legacy one tap method.
     * This is not recommended to use for new applications that support Android 14(API 34) and above.
     *
     * @param context [Context] of the application
     * @param googleAuthenticateResultLauncher [ActivityResultLauncher] to launch the Google authentication intent
     */
    suspend fun handleGoogleNativeLegacyAuthentication(
        context: Context,
        googleAuthenticateResultLauncher: ActivityResultLauncher<Intent>
    )

    /**
     * Handle the Google native authentication result using the legacy one tap method.
     * This method will authenticate the user with the Google Native Authentication using the legacy one tap method.
     * This is not recommended to use for new applications that support Android 14(API 34) and above.
     *
     * @param result The [ActivityResult] object that contains the result of the Google authentication process
     *
     * @return The Google native authenticator parameters [LinkedHashMap] that contains the ID Token and the Auth Code
     */
    suspend fun handleGoogleNativeLegacyAuthenticateResult(result: ActivityResult)
            : LinkedHashMap<String, String>?
}
