package io.wso2.android.api_authenticator.sdk.models.autheniticator_type.meta_data

@Deprecated("This class is deprecated. Directly use [AuthenticatorTypeMetaData]")
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
    i18nKey = i18nKey,
    promptType = promptType,
    additionalData = additionalData
) {
    data class GoogleAdditionalData(
        /**
         * Nonce for google authentication
         */
        override val nonce: String,
        /**
         * Client id for google authentication
         */
        override val clientId: String,
        /**
         * Scope for google authentication
         */
        override val scope: String
    ): AuthenticatorTypeAdditionalData(
        nonce,
        clientId,
        scope
    )
}