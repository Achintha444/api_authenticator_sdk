package io.wso2.android.api_authenticator.sdk.models.autheniticator_type.meta_data

@Deprecated("This class is deprecated. Directly use [AuthenticatorTypeMetaData]")
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
    promptType = promptType,
    params = params,
)
