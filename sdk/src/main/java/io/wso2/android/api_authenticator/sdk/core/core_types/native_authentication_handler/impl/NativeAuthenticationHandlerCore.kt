package io.wso2.android.api_authenticator.sdk.core.core_types.native_authentication_handler.impl

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.core_types.native_authentication_handler.NativeAuthenticationHandlerCoreDef
import io.wso2.android.api_authenticator.sdk.core.di.NativeAuthenticationHandlerCoreContainer
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.google_native_authentication_handler.GoogleNativeAuthenticationHandlerManager
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.google_native_legacy_authentication_handler.GoogleNativeLegacyAuthenticationHandlerManager
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.redirect_authentication_handler.RedirectAuthenticationHandlerManager
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import java.lang.ref.WeakReference

class NativeAuthenticationHandlerCore private constructor(
    private val authenticationCoreConfig: AuthenticationCoreConfig
) : NativeAuthenticationHandlerCoreDef {
    /**
     * Instance of the [GoogleNativeAuthenticationHandlerManager] that will be used throughout the application
     */
    private val googleNativeAuthenticationHandlerManager: GoogleNativeAuthenticationHandlerManager by lazy {
        NativeAuthenticationHandlerCoreContainer.getGoogleNativeAuthenticationHandlerManager(
            authenticationCoreConfig
        )
    }

    /**
     * Instance of the [GoogleNativeLegacyAuthenticationHandlerManager] that will be used throughout the application
     */
    private val googleNativeLegacyAuthenticationHandlerManager:
            GoogleNativeLegacyAuthenticationHandlerManager by lazy {
        NativeAuthenticationHandlerCoreContainer.getGoogleNativeLegacyAuthenticationHandlerManager(
            authenticationCoreConfig
        )
    }

    /**
     * Instance of the [RedirectAuthenticationHandlerManager] that will be used throughout the application
     */
    private val redirectAuthenticationHandlerManager: RedirectAuthenticationHandlerManager by lazy {
        NativeAuthenticationHandlerCoreContainer.getRedirectAuthenticationHandlerManager()
    }

    companion object {
        /**
         * Instance of the [NativeAuthenticationHandlerCore] that will be used throughout the application
         */
        private var nativeAuthenticationCoreInstance =
            WeakReference<NativeAuthenticationHandlerCore?>(null)

        /**
         * Initialize the AuthenticationCore instance and return the instance.
         *
         * @param authenticationCoreConfig Configuration of the Authenticator [AuthenticationCoreConfig]
         *
         * @return Initialized [NativeAuthenticationHandlerCore] instance
         */
        fun getInstance(authenticationCoreConfig: AuthenticationCoreConfig): NativeAuthenticationHandlerCore {
            var nativeAuthenticationCore = nativeAuthenticationCoreInstance.get()
            if (nativeAuthenticationCore == null ||
                nativeAuthenticationCore.authenticationCoreConfig != authenticationCoreConfig
            ) {
                nativeAuthenticationCore = NativeAuthenticationHandlerCore(authenticationCoreConfig)
                nativeAuthenticationCoreInstance = WeakReference(nativeAuthenticationCore)
            }
            return nativeAuthenticationCore
        }
    }

    /**
     * Handle the Google Native Authentication.
     * This method will authenticate the user with the Google Native Authentication.
     * This method is recommended to use for new applications that support Android 14(API 34) and above.
     *
     * @param context Context of the application
     *
     * @return idToken sent by the Google Native Authentication
     */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun handleGoogleNativeAuthentication(context: Context): String? =
        googleNativeAuthenticationHandlerManager.authenticateWithGoogleNative(context)

    /**
     * Handle the Google Native Authentication result using the legacy one tap method.
     * This method will authenticate the user with the Google Native Authentication using the legacy one tap method.
     * This is not recommended to use for new applications that support Android 14(API 34) and above.
     *
     * @param context [Context] of the application
     * @param googleAuthenticateResultLauncher [ActivityResultLauncher] to launch the Google authentication intent
     */
    override suspend fun handleGoogleNativeLegacyAuthentication(
        context: Context,
        googleAuthenticateResultLauncher: ActivityResultLauncher<Intent>
    ) {
        googleNativeLegacyAuthenticationHandlerManager.authenticateWithGoogleNativeLegacy(
            context,
            googleAuthenticateResultLauncher
        )
    }

    /**
     * Handle the Google native authentication result using the legacy one tap method.
     * This method will authenticate the user with the Google Native Authentication using the legacy one tap method.
     * This is not recommended to use for new applications that support Android 14(API 34) and above.
     *
     * @param result The [ActivityResult] object that contains the result of the Google authentication process
     *
     * @return The Google native authenticator parameters [LinkedHashMap] that contains the ID Token and the Auth Code
     */
    override suspend fun handleGoogleNativeLegacyAuthenticateResult(result: ActivityResult)
            : LinkedHashMap<String, String>? =
        googleNativeLegacyAuthenticationHandlerManager.handleGoogleNativeLegacyAuthenticateResult(
            result
        )

    /**
     * Handle the redirect authentication process.
     * This method will redirect the user to the authenticator's authentication page.
     *
     * @param context The context of the application
     * @param authenticatorType The authenticator type to redirect the user
     *
     * @return The authentication parameters extracted from the redirect URI
     */
    override suspend fun handleRedirectAuthentication(
        context: Context,
        authenticatorType: AuthenticatorType
    ): LinkedHashMap<String, String>? =
        redirectAuthenticationHandlerManager.redirectAuthenticate(context, authenticatorType)
}
