package io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.redirect_authentication_handler

import android.content.Context
import android.net.Uri
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType

/**
 * This manager is responsible for handling the redirect authentication process
 * using the redirection prompt type
 */
interface RedirectAuthenticationHandlerManager {
    /**
     * Redirect the user to the authenticator's authentication page.
     *
     * @param context The context of the application
     * @param authenticatorType The authenticator type to redirect the user
     */
    suspend fun redirectAuthenticate(
        context: Context,
        authenticatorType: AuthenticatorType
    ): LinkedHashMap<String, String>?

    /**
     * Handle the redirect URI and authenticate the user with the selected authenticator.
     *
     * @param context The context of the application
     * @param deepLink The deep link URI that is received from the redirect URI
     *
     * @return The authentication parameters extracted from the redirect URI
     */
    fun handleRedirectUri(context: Context, deepLink: Uri)
}