package io.wso2.android.api_authenticator.sdk.models.meta_data

data class BasicAuthenticatorTypeMetaData (
    /**
     * Prompt type
     */
    override val promptType: String?,
    /**
     * Params
     */
    override var params: ArrayList<Param>?,
): MetaData(
    null,
    promptType,
    params
)
