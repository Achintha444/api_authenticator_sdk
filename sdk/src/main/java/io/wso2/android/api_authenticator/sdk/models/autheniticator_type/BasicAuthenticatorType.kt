package io.wso2.android.api_authenticator.sdk.models.autheniticator_type

import io.wso2.android.api_authenticator.sdk.models.meta_data.BasicAuthenticatorTypeMetaData

/**
 * Basic authenticator type
 */
data class BasicAuthenticatorType(
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
    override val metadata: BasicAuthenticatorTypeMetaData?,
    /**
     * Required parameters of the basic authenticator type
     */
    override val requiredParams: List<String>?
): AuthenticatorType(
    authenticatorId,
    authenticator,
    idp,
    metadata,
    requiredParams
)
