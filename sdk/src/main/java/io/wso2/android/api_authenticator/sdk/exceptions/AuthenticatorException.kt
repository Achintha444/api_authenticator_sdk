package io.wso2.android.api_authenticator.sdk.exceptions

/**
 * Exception to be thrown to the exception related to the Authenticator
 *
 * @property message Message related to the exception
 */
class AuthenticatorException(override val message: String?): Exception(message) {
    companion object {
        /**
         * Authenticator exception TAG
         */
        const val AUTHENTICATOR_EXCEPTION = "Authenticator Exception"

        /**
         * Message to be shown when authenticator is not initialized
         */
        const val AUTHENTICATOR_NOT_INITIALIZED = "Authenticator is not initialized"
    }

    override fun toString(): String {
        return "${AUTHENTICATOR_EXCEPTION}: $message"
    }

    /**
     * Print the exception
     */
    fun printException() {
        println(toString())
    }
}
