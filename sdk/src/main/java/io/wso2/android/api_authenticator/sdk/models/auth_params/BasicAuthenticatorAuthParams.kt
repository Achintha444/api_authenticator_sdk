package io.wso2.android.api_authenticator.sdk.models.auth_params

/**
 * Authenticator parameters class - For Basic Authenticator
 */
data class BasicAuthenticatorAuthParams(
    /**
     * Username of the user
     */
    override val username: String,
    /**
     * Password of the user
     */
    override val password: String
): AuthParams(
    username = username,
    password = password
) {
    /**
     * Get the parameter body for the authenticator to be sent to the server
     *
     * @return LinkedHashMap<String, String> - Parameter body for the authenticator
     * ex: [<"username", username>, <"password", password>]
     */
    override fun getParameterBodyAuthenticator(): LinkedHashMap<String, String> {
        val paramBody = LinkedHashMap<String, String>()
        paramBody["username"] = username
        paramBody["password"] = password

        return paramBody
    }
}
