package io.wso2.android.api_authenticator.sdk

import io.wso2.android.api_authenticator.sdk.exceptions.AuthenticatorException
import java.lang.ref.WeakReference

/**
 * Authenticator class to handle authentication.
 *
 * @property authorizeUri Authorization endpoint
 * @property clientId Client id of the application
 * @property scope Scope of the application (ex: openid profile email)
 */
class Authenticator private constructor(
    private val authorizeUri: String,
    private val clientId: String,
    private val scope: String
) {
    companion object {
        /**
         * Instance of the Authenticator that will be used throughout the application
         */
        private var authenticatorInstance = WeakReference<Authenticator?>(null)

        /**
         * Initialize the Authenticator instance and return the instance.
         *
         * @param authorizeUri Authorization endpoint
         * @param clientId Client id of the application
         * @param scope Scope of the application (ex: openid profile email)
         */
        fun getInstance(
            authorizeUri: String,
            clientId: String,
            scope: String
        ): Authenticator {
            var authenticator = authenticatorInstance.get()
            if (authenticator == null) {
                authenticator = Authenticator(authorizeUri, clientId, scope)
                authenticatorInstance = WeakReference(authenticator)
            }
            return authenticator
        }

        /**
         * Get the Authenticator instance.
         * This method will return null if the Authenticator instance is not initialized.
         *
         * @throws AuthenticatorException If the Authenticator instance is not initialized
         */
        fun getInstance(): Authenticator {
            return authenticatorInstance.get()
                ?: throw AuthenticatorException(AuthenticatorException.AUTHENTICATOR_NOT_INITIALIZED)
        }
    }
}
