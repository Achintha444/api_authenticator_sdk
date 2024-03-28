package io.wso2.android.api_authenticator.sdk.core.core_types.native_authentication_handler.impl

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.core_types.native_authentication_handler.NativeAuthenticationHandlerCoreDef
import io.wso2.android.api_authenticator.sdk.core.di.NativeAuthenticationHandlerCoreContainer
import io.wso2.android.api_authenticator.sdk.core.managers.authenticator.AuthenticatorManager
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.google_native_authentication_handler.GoogleNativeAuthenticationHandlerManager
import java.lang.ref.WeakReference

class NativeAuthenticationHandlerCore private constructor(
    private val authenticationCoreConfig: AuthenticationCoreConfig
) : NativeAuthenticationHandlerCoreDef {
    /**
     * Instance of the [AuthenticatorManager] that will be used throughout the application
     */
    private val googleNativeAuthenticationHandlerManager: GoogleNativeAuthenticationHandlerManager by lazy {
        NativeAuthenticationHandlerCoreContainer.getGoogleNativeAuthenticationHandlerManager(
            authenticationCoreConfig
        )
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
     *
     * @param context Context of the application
     *
     * @return idToken sent by the Google Native Authentication
     */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun handleGoogleNativeAuthentication(context: Context): String? =
        googleNativeAuthenticationHandlerManager.authenticateWithGoogleNative(context)
}
