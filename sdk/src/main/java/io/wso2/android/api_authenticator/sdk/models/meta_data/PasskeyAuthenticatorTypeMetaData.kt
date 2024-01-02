package io.wso2.android.api_authenticator.sdk.models.meta_data

data class PasskeyAuthenticatorTypeMetaData(
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
    override val additionalData: PasskeyAdditionalData
) : AuthenticatorTypeMetaData(
    i18nKey,
    promptType,
    null,
    additionalData
) {
    data class PasskeyAdditionalData(
        /**
         * Challenge data for passkey authentication
         */
        val challengeData: String
    ): AuthenticatorTypeAdditionalData()
}