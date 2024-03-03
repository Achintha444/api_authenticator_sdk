package io.wso2.android.api_authenticator.sdk.models.authorize_flow

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
    fun toJsonString(): String {
        return JsonUtil.getJsonString(this)
    }
}

