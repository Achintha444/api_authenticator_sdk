package io.wso2.android.api_authenticator.sdk.models.exceptions

/**
 * Exception to be thrown to the exception related to [AuthnManager]
 *
 * @property message Message related to the exception
 */
internal class AuthnManagerException (
    override val message: String?
): Exception(message) {
    companion object {
        /**
         * Authenticator exception TAG
         */
        const val AUTHN_MANAGER_EXCEPTION = "AuthnManager Exception"

        /**
         * Message to be shown when authentication is not completed
         */
        const val AUTHENTICATION_NOT_COMPLETED = "Authentication is not completed. Response returned FAIL_INCOMPLETE"
    }

    override fun toString(): String {
        return "$AUTHN_MANAGER_EXCEPTION: $message"
    }
}
