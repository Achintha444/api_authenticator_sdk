package io.wso2.android.api_authenticator.sdk.models.autheniticator_type.meta_data

data class TotpAuthenticatorTypeMetaData(
    /**
     * I18n key of the param
     */
    override val i18nKey: String,
    /**
     * Prompt type
     */
    override val promptType: String?,
    /**
     * Params
     */
    override var params: ArrayList<AuthenticatorTypeParam>?,
): AuthenticatorTypeMetaData(
    i18nKey = i18nKey,
    promptType = promptType,
    params = params
)
