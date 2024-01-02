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
    val additionalData: GoogleAdditionalData
) : AuthenticatorTypeMetaData(
    i18nKey,
    promptType,
    null
) {
    data class GoogleAdditionalData(
        val nonce: String?,
        val clientId: String?
    )
}