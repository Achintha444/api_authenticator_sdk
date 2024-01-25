package io.wso2.android.api_authenticator.sdk.models.auth_params

/**
 * Authenticator parameters class - For TOTP Authenticator
 */
data class TotpAuthenticatorTypeAuthParams (
    /**
     * Code retrieved from the authenticator application
     */
    override val totp: String
): AuthParams(
    totp = totp
) {
    /**
     * Get the parameter body for the authenticator to be sent to the server
     *
     * @return LinkedHashMap<String, String> - Parameter body for the authenticator
     * ex: [<"totp", totp>]
     */
    override fun getParameterBodyAuthenticator(): LinkedHashMap<String, String> {
        val paramBody = LinkedHashMap<String, String>()
        paramBody["totp"] = totp

        return paramBody
    }
}
