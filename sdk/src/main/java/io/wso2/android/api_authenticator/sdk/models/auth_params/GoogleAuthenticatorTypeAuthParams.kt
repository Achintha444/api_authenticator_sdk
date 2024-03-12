package io.wso2.android.api_authenticator.sdk.models.auth_params

/**
 * Authenticator parameters class - For Google Authenticator
 */
data class GoogleAuthenticatorTypeAuthParams(
    /**
     * access token retrieved from the Google authenticator
     */
    override val accessToken: String,
    /**
     * id token retrieved from the Google authenticator
     */
    override val idToken: String
) : AuthParams(
    accessToken = accessToken,
    idToken = idToken
) {
    /**
     * Get the parameter body for the authenticator to be sent to the server
     *
     * @return LinkedHashMap<String, String> - Parameter body for the authenticator
     * ex: [<"accessToken", accessToken>, <"idToken", idToken>]
     */
    override fun getParameterBodyAuthenticator(requiredParams: List<String>)
            : LinkedHashMap<String, String> {
        val paramBody = LinkedHashMap<String, String>()
        paramBody[requiredParams[0]] = accessToken
        paramBody[requiredParams[1]] = idToken

        return paramBody
    }
}