package io.wso2.android.api_authenticator.sdk.models.autheniticator

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.wso2.android.api_authenticator.sdk.models.autheniticator.meta_data.AuthenticatorMetaData
import io.wso2.android.api_authenticator.sdk.util.JsonUtil

/**
 * Class to represent an Authenticator
 */
@JsonIgnoreProperties(ignoreUnknown = true)
open class Authenticator(
    /**
     * Id of the authenticator
     */
    open val authenticatorId: String,
    /**
     * Name of the authenticator
     */
    open val authenticator: String?,
    /**
     * Id of the idp of the authenticator
     */
    open val idp: String?,
    /**
     * Metadata of the authenticator
     */
    open val metadata: AuthenticatorMetaData?,
    /**
     * Required params that should be sent to the server for authentication in this authenticator
     */
    open val requiredParams: List<String>?
) {
    fun toJsonString(): String {
        return JsonUtil.getJsonString(this)
    }
}
