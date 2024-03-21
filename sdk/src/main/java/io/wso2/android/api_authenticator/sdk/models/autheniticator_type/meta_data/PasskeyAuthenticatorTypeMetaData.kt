package io.wso2.android.api_authenticator.sdk.models.autheniticator_type.meta_data

@Deprecated("This class is deprecated. Directly use [AuthenticatorTypeMetaData]")
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
    i18nKey = i18nKey,
    promptType = promptType,
    additionalData = additionalData
) {
    data class PasskeyAdditionalData(
        /**
         * Challenge data for passkey authentication
         */
        override val challengeData: String
    ): AuthenticatorTypeAdditionalData(
        challengeData = challengeData
    )
}