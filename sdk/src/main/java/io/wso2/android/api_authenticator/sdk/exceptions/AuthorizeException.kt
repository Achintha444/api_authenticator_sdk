package io.wso2.android.api_authenticator.sdk.exceptions

/**
 * Exception to be thrown when authorization fails
 *
 * @property message Message related to the exception
 */
class AuthorizeException(override val message: String?): Exception(message)
