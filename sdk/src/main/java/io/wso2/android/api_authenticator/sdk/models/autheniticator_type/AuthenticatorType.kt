package io.wso2.android.api_authenticator.sdk.models.autheniticator_type

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.meta_data.AuthenticatorTypeMetaData
import io.wso2.android.api_authenticator.sdk.util.JsonUtil

/**
 * AuthenticatorType model class
 */
@JsonIgnoreProperties(ignoreUnknown = true)
open class AuthenticatorType(
    /**
     * Id of the authenticator type
     */
    open val authenticatorId: String,
    /**
     * Name of the authenticator type
     */
    open val authenticator: String?,
    /**
     * Id of the idp of the authenticator type
     */
    open val idp: String?,
    /**
     * Metadata of the authenticator type
     */
    open val metadata: AuthenticatorTypeMetaData?,
    /**
     * Required params that should be sent to the server for authentication in this authenticator type
     */
    open val requiredParams: List<String>?
) {
    fun toJsonString(): String {
        return JsonUtil.getJsonString(this)
    }
}
