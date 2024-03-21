package io.wso2.android.api_authenticator.sdk.models.autheniticator_type

import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.meta_data.PasskeyAuthenticatorTypeMetaData

@Deprecated("This class is deprecated. Directly use [AuthenticatorType]")
data class PasskeyAuthenticatorType(
    /**
     * Authenticator id
     */
    override val authenticatorId: String,
    /**
     * Authenticator name
     */
    override val authenticator: String,
    /**
     * Identity provider
     */
    override val idp: String,
    /**
     * Metadata of the basic authenticator type
     */
    override val metadata: PasskeyAuthenticatorTypeMetaData?,
    /**
     * Required parameters of the basic authenticator type
     */
    override val requiredParams: List<String>?
) : AuthenticatorType(
    authenticatorId,
    authenticator,
    idp,
    metadata,
    requiredParams
) {
    companion object {
        const val AUTHENTICATOR_TYPE = "Passkey"
    }
}
