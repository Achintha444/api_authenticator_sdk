package io.wso2.android.api_authenticator.sdk.models.exceptions

/**
 * Exception to be thrown to the exception related to [TokenManager]
 */
class TokenManagerException (
    override val message: String?
): Exception(message) {
    companion object {
        /**
         * Token manager exception TAG
         */
        const val TOKEN_MANAGER_EXCEPTION = "TokenManager Exception"

        /**
         * Message to be shown when token is not initialized
         */
        const val TOKEN_NOT_SAVED = "Token is not saved due to save failure"

        /**
         *
         */
        const val CANNOT_GET_TOKEN = "Cannot get the token from the token data store due to an error"
    }

    override fun toString(): String {
        return "$TOKEN_MANAGER_EXCEPTION: $message"
    }
}
