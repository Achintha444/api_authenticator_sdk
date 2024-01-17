package io.wso2.android.api_authenticator.sdk.services.authorization_service

import io.wso2.android.api_authenticator.sdk.util.JsonUtil
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Builder function related to the Authenticator.
 */
internal class AuthorizationServiceRequestBuilder {

    /**
     * Build the request to authorize the application.
     *
     * @param authorizeUri Authorization endpoint
     * @param clientId Client id of the application
     * @param scope Scope of the application (ex: openid profile email)
     *
     * @return [okhttp3.Request] to authorize the application
     */
    internal fun authorizeRequestBuilder(
        authorizeUri: String,
        clientId: String,
        scope: String
    ): Request {
        val formBody: RequestBody = FormBody.Builder()
            .add("client_id", clientId)
            .add("scope", scope)
            .add("response_type", "code")
            .add("response_mode", "direct")
            .build()

        val requestBuilder: Request.Builder = Request.Builder().url(authorizeUri)
        requestBuilder.addHeader("Accept", "application/json")
        requestBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded")

        return requestBuilder.post(formBody).build()
    }

    /**
     * Build the request to get details of the authenticator type.
     *
     * @param authnUri Authentication next step endpoint
     * @param flowId Flow id of the authentication flow
     * @param authenticatorTypeId Authenticator type id of the authenticator
     *
     * @return [okhttp3.Request] to get details of the authenticator type
     */
    internal fun getAuthenticatorTypeRequestBuilder(
        authnUri: String,
        flowId: String,
        authenticatorTypeId: String
    ): Request {

        /**
         * Build request body to get details of the authenticator type
         *
         * @param flowId Flow id of the authentication flow
         * @param authenticatorTypeId Authenticator type id of the authenticator
         *
         * @return [okhttp3.RequestBody]` to get details of the authenticator type
         */
        fun getRequestBodyForAuthenticatorType(
            flowId: String,
            authenticatorTypeId: String
        ): RequestBody {

            val authBody = LinkedHashMap<String, Any>()
            authBody["flowId"] = flowId

            val selectedAuthenticator = LinkedHashMap<String, String>()
            selectedAuthenticator["authenticatorId"] = authenticatorTypeId

            authBody["selectedAuthenticator"] = selectedAuthenticator;

            return JsonUtil.getJsonObject(authBody).toString()
                .toRequestBody("application/json".toMediaTypeOrNull())
        }

        val formBody: RequestBody =  getRequestBodyForAuthenticatorType(
            flowId,
            authenticatorTypeId
        )

        val requestBuilder: Request.Builder = Request.Builder().url(authnUri)

        return requestBuilder.post(formBody).build()
    }
}