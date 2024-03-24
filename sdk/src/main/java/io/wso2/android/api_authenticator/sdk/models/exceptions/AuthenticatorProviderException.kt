package io.wso2.android.api_authenticator.sdk.models.exceptions

/**
 * Exception to be thrown to the exception related to [AuthenticatorProvider]
 *
 * @property message Message related to the exception
 */
class AuthenticatorProviderException (
    override val message: String?
): Exception(message) {
    companion object {
        /**
         * AuthenticatorProvider Exception
         */
        const val AUTHENTICATOR_PROVIDER_EXCEPTION = "AuthenticatorProvider Exception"
        /**
         * Message for the case where the authenticator is not found
         */
        const val AUTHENTICATOR_NOT_FOUND = "Authenticator not found"
        /**
         * Message for the case where the authenticator is not supported
         */
        const val NOT_REDIRECT_PROMPT = "Authenticator does not support redirect prompt"
        /**
         * Message for the case where the redirect URI is not found
         */
        const val REDIRECT_URI_NOT_FOUND = "Redirect URI not found"
        /**
         * Message for the case where the google web client id is not found
         */
        const val GOOGLE_WEB_CLIENT_ID_NOT_FOUND = "Google web client id not found"
        /**
         * Message for the case where the Google auth code or id token is not found
         */
        const val GOOGLE_AUTH_CODE_OR_ID_TOKEN_NOT_FOUND = "Google auth code or id token not found"


    }

    override fun toString(): String {
        return "$AUTHENTICATOR_PROVIDER_EXCEPTION: $message"
    }
}
