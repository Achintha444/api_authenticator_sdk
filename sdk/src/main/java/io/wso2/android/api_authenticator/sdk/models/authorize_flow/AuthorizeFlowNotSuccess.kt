package io.wso2.android.api_authenticator.sdk.models.authorize_flow

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.util.JsonUtil

/**
 * Authorize flow data class. Which is used to hold the data of an authentication flow.
 *
 * @property flowId Id of the authentication flow
 * @property flowStatus Status of the authentication flow
 * @property flowType Type of the authentication flow
 * @property nextStep Next step of the authentication flow
 * @property links Links of the authentication flow
 */
data class AuthorizeFlowNotSuccess(
    override val flowStatus: String,
    val flowId: String,
    val flowType: String,
    var nextStep: AuthorizeFlowNotSuccessNextStep,
    val links: Any
): AuthorizeFlow(flowStatus) {
    /**
     * Authorize flow next step data class.
     *
     * @property stepType Type of the next step
     * @property authenticatorTypes List of authenticator types of the next step
     */
    data class AuthorizeFlowNotSuccessNextStep(
        val stepType: String,
        var authenticatorTypes: ArrayList<AuthenticatorType>
    ) {
        /**
         * Convert the object to a json string
         */
        override fun toString(): String {
            return JsonUtil.getJsonString(this)
        }
    }

    companion object {
        /**
         * Convert a json string to a [AuthorizeFlowNotSuccess] object.
         *
         * @param jsonString Json string to be converted
         *
         * @return [AuthorizeFlowNotSuccess] converted from the json string
         */
        fun fromJson(jsonString: String): AuthorizeFlowNotSuccess {
            val stepTypeReference = object : TypeReference<AuthorizeFlowNotSuccess>() {}
            val jsonNodeAuthorizeFlow: JsonNode = JsonUtil.getJsonObject(jsonString)

            return JsonUtil.jsonNodeToObject(jsonNodeAuthorizeFlow, stepTypeReference);
        }
    }
}
