package io.wso2.android.api_authenticator.sdk.models.authorize_flow

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.util.JsonUtil

/**
 * Authorize flow data class. Which is used to hold the data of an authentication flow.
 *
 * @property flowStatus Status of the authentication flow
 */
abstract class AuthorizeFlow(open val flowStatus: String) {
    /**
     * Convert the object to a json string
     */
    override fun toString(): String {
        return JsonUtil.getJsonString(this)
    }
}

