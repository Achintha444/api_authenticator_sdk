package io.wso2.android.api_authenticator.sdk.exceptions

/**
 * Exception to be thrown to the exception related to the Authenticator
 *
 * @property message Message related to the exception
 */
class AuthenticationCoreException(
    override val message: String?
): Exception(message) {
    companion object {
        /**
         * Authenticator exception TAG
         */
        const val AUTHORIZATION_SERVICE_EXCEPTION = "Authorization Service Exception"

        /**
         * Message to be shown when authenticator is not initialized
         */
        const val AUTHORIZATION_SERVICE_NOT_INITIALIZED = "Authorization Service is not initialized"

        /**
         * Message to be shown when authentication is not completed
         */
        const val AUTHENTICATION_NOT_COMPLETED = "Authentication is not completed. Response returned FAIL_INCOMPLETE"
    }

    override fun toString(): String {
        return "${AUTHORIZATION_SERVICE_EXCEPTION}: $message"
    }

    /**
     * Print the exception
     */
    fun printException() {
        println(toString())
    }
}
