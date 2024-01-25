package io.wso2.android.api_authenticator.sdk.exceptions

/**
 * Exception to be thrown to the exception related to the [AppAuthManager]
 *
 * @property message Message related to the exception
 * @property exceptionMessage Message related to the exception
 */
class AppAuthManagerException(
    override val message: String?,
    private val exceptionMessage: String? = null
): Exception(message) {
    companion object {
        /**
         * Authenticator exception TAG
         */
        const val APP_AUTH_MANAGER_EXCEPTION = "App Auth Manager Exception"

        /**
         * Message to be shown when authenticator is not initialized
         */
        const val TOKEN_REQUEST_FAILED = "Token request failed"
    }

    override fun toString(): String {
        return "${APP_AUTH_MANAGER_EXCEPTION}: $message $exceptionMessage"
    }

    /**
     * Print the exception
     */
    fun printException() {
        println(toString())
    }
}
