package io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.redirect.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.redirect.RedirectAuthenticationHandlerManager
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.exceptions.RedirectAuthenticationException
import io.wso2.android.api_authenticator.sdk.models.prompt_type.PromptTypes
import kotlinx.coroutines.CompletableDeferred
import java.lang.ref.WeakReference

/**
 * Implementation of [RedirectAuthenticationHandlerManager]
 * This manager is responsible for handling the redirect authentication process
 * using the redirection prompt type
 */
class RedirectAuthenticationHandlerManagerImpl private constructor() :
    RedirectAuthenticationHandlerManager {
    companion object {
        /**
         * Instance of the [RedirectAuthenticationHandlerManagerImpl] that will be used throughout the application
         */
        private var redirectAuthenticationHandlerManagerImplInstance:
                WeakReference<RedirectAuthenticationHandlerManagerImpl> =
            WeakReference(null)

        /**
         * Initialize the [RedirectAuthenticationHandlerManagerImpl] instance and return the instance.
         *
         * @return Initialized [RedirectAuthenticationHandlerManagerImpl] instance
         */
        fun getInstance(): RedirectAuthenticationHandlerManagerImpl {
            var redirectAuthenticationHandlerImpl =
                redirectAuthenticationHandlerManagerImplInstance.get()
            if (redirectAuthenticationHandlerImpl == null) {
                redirectAuthenticationHandlerImpl = RedirectAuthenticationHandlerManagerImpl()
                redirectAuthenticationHandlerManagerImplInstance =
                    WeakReference(redirectAuthenticationHandlerImpl)
            }
            return redirectAuthenticationHandlerImpl
        }
    }

    /**
     * The selected authenticator to redirect the user
     */
    private var selectedAuthenticator: AuthenticatorType? = null

    /**
     * The authentication parameters extracted from the redirect URI
     */
    private var authenticatorAuthParamsMap: LinkedHashMap<String, String>? = null

    /**
     * Deferred object to wait for the result of the redirect authentication process.
     *
     * TODO: Move to the Core module
     */
    private val redirectAuthenticationResultDeferred: CompletableDeferred<Unit> by lazy {
        CompletableDeferred()
    }

    /**
     * Redirect the user to the authenticator's authentication page.
     *
     * @param context The context of the application
     * @param authenticatorType The authenticator type to redirect the user
     */
    override suspend fun redirectAuthenticate(
        context: Context,
        authenticatorType: AuthenticatorType
    ): LinkedHashMap<String, String>? {
        // Retrieving the prompt type of the authenticator
        val promptType: String? = authenticatorType.metadata?.promptType

        if (promptType == PromptTypes.REDIRECTION_PROMPT.promptType) {
            // Retrieving the redirect URI of the authenticator
            val redirectUri: String? = authenticatorType.metadata?.additionalData?.redirectUrl

            if (redirectUri.isNullOrEmpty()) {
                throw (RedirectAuthenticationException(
                    RedirectAuthenticationException
                        .REDIRECT_URI_NOT_FOUND
                ))
            } else {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(redirectUri))
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)

                selectedAuthenticator = authenticatorType

                redirectAuthenticationResultDeferred.await()

                if (redirectAuthenticationResultDeferred.isCompleted) {
                    return authenticatorAuthParamsMap
                } else {
                    throw (RedirectAuthenticationException(
                        RedirectAuthenticationException.REDIRECT_URI_NOT_FOUND
                    ))
                }
            }
        } else {
            throw (RedirectAuthenticationException(
                RedirectAuthenticationException.NOT_REDIRECT_PROMPT
            ))
        }
    }

    /**
     * Handle the redirect URI and authenticate the user with the selected authenticator.
     *
     * @param context The context of the application
     * @param deepLink The deep link URI that is received from the redirect URI
     *
     * @return The authentication parameters extracted from the redirect URI
     */
    override fun handleRedirectUri(context: Context, deepLink: Uri) {
        // Setting up the deferred object to wait for the result
        if (selectedAuthenticator != null) {
            val requiredParams: List<String> = selectedAuthenticator!!.requiredParams!!

            // Extract required parameters from the authenticator type
            val authParamsMap: LinkedHashMap<String, String> = LinkedHashMap()

            for (param in requiredParams) {
                val paramValue: String? = deepLink.getQueryParameter(param)

                if (paramValue != null) {
                    authParamsMap[param] = paramValue
                }
            }

            // Finish the [RedirectUriReceiverActivity] activity
            if (context is ComponentActivity) {
                context.finish()
            }

            authenticatorAuthParamsMap = authParamsMap
        } else {
            throw (RedirectAuthenticationException(
                RedirectAuthenticationException.AUTHENTICATOR_NOT_SELECTED
            ))
        }

        redirectAuthenticationResultDeferred.complete(Unit)
    }
}
