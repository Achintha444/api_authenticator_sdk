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
    val additionalData: PasskeyAdditionalData
) : AuthenticatorTypeMetaData(
    i18nKey,
    promptType,
    null
) {
    data class PasskeyAdditionalData(
        val challengeData: String
    )
}