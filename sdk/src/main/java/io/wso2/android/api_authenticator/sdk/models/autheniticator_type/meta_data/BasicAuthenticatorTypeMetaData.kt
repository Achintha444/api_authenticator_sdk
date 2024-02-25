package io.wso2.android.api_authenticator.sdk.models.autheniticator_type.meta_data

data class BasicAuthenticatorTypeMetaData (
    /**
     * Prompt type
     */
    override val promptType: String?,
    /**
     * Params
     */
    override var params: ArrayList<AuthenticatorTypeParam>?,
): AuthenticatorTypeMetaData(
    null,
    promptType,
    params,
    null
)
