package io.wso2.android.api_authenticator.sdk.models.meta_data

data class GoogleAuthenticatorTypeMetaData(
    /**
     * I18n key of the param
     */
    override val i18nKey: String?,
    /**
     * Prompt type
     */
    override val promptType: String?,
    /**
     * Additional data
     */
    override val additionalData: GoogleAdditionalData
) : AuthenticatorTypeMetaData(
    i18nKey,
    promptType,
    null,
    additionalData
) {
    data class GoogleAdditionalData(
        /**
         * Nonce for google authentication
         */
        val nonce: String?,
        /**
         * Client id for google authentication
         */
        val clientId: String?
    ): AuthenticatorTypeAdditionalData()
}