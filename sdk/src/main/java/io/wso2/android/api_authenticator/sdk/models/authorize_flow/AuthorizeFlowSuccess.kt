package io.wso2.android.api_authenticator.sdk.models.authorize_flow

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import io.wso2.android.api_authenticator.sdk.util.JsonUtil

/**
 * Authorize flow success data class. Which is used to hold the data of an successful authentication flow.
 *
 * @property flowStatus Status of the authentication flow
 * @property authData Authentication data of the authentication flow
 */
data class AuthorizeFlowSuccess(
    override val flowStatus: String,
    val authData: AuthData,
): AuthorizeFlow(flowStatus) {
    /**
     * Authentication data data class which is used to hold the authentication data of a successful authentication flow.
     *
     * @property code Code of the authentication flow
     * @property session_state Session state of the authentication flow
     */
    data class AuthData(
        val code: String,
        val session_state: String
    )

    companion object {
        /**
         * Convert a json string to a [AuthorizeFlowSuccess] object.
         *
         * @param jsonString Json string to be converted
         *
         * @return [AuthorizeFlowSuccess] converted from the json string
         */
        fun fromJson(jsonString: String): AuthorizeFlowSuccess {
            val stepTypeReference = object : TypeReference<AuthorizeFlowSuccess>() {}
            val jsonNodeAuthorizeFlow: JsonNode = JsonUtil.getJsonObject(jsonString)

            return JsonUtil.jsonNodeToObject(jsonNodeAuthorizeFlow, stepTypeReference);
        }
    }
}
