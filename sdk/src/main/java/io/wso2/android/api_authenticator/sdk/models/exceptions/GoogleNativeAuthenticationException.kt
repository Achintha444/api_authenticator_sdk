package io.wso2.android.api_authenticator.sdk.models.exceptions

/**
 * Exception to be thrown to the exception related to the Google Native Authentication
 */
class GoogleNativeAuthenticationException (
    override val message: String?
): Exception(message) {
    companion object {
        /**
         * Google Native Authentication exception TAG
         */
        const val GOOGLE_NATIVE_AUTHENTICATION_EXCEPTION = "Google Native Authentication Exception"

        /**
         * Message to be shown when Google Web Client ID is not set
         */
        const val GOOGLE_WEB_CLIENT_ID_NOT_SET = "Google Web Client ID is not set"
    }

    override fun toString(): String {
        return "$GOOGLE_NATIVE_AUTHENTICATION_EXCEPTION: $message"
    }
}
