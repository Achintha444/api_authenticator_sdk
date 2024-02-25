package io.wso2.android.api_authenticator.sdk.core.managers.authn.impl

import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.util.JsonUtil
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Builder function related to the Authenticator.
 */
internal object AuthnManagerImplRequestBuilder {

    /**
     * Build the request to authorize the application.
     *
     * @param authorizeUri Authorization endpoint
     * @param clientId Client id of the application
     * @param scope Scope of the application (ex: openid profile email)
     * @param integrityToken Client attestation integrity token
     *
     * @return [okhttp3.Request] to authorize the application
     */
    internal fun authorizeRequestBuilder(
        authorizeUri: String,
        clientId: String,
        scope: String,
        integrityToken: String? = null
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

        // Pass the client attestation token if it is not null
        if (integrityToken != null) {
            requestBuilder.addHeader("x-client-attestation", integrityToken)
        }

        return requestBuilder.post(formBody).build()
    }

    /**
     * Build the request for the authentication.
     * This request will be used to get the next step of the authentication flow.
     *
     * @param authnUri Authentication next step endpoint
     * @param flowId Flow id of the authentication flow
     * @param authenticatorType Authenticator type of the selected authenticator
     * @param authenticatorAuthParams Authenticator parameters of the selected authenticator
     *
     * @return [okhttp3.Request] to get the next step of the authentication flow
     */
    internal fun authenticateRequestBuilder(
        authnUri: String,
        flowId: String,
        authenticatorType: AuthenticatorType,
        authenticatorAuthParams: AuthParams,
    ): Request {
        val authBody = LinkedHashMap<String, Any>()
        authBody["flowId"] = flowId

        val selectedAuthenticator = LinkedHashMap<String, Any>()
        selectedAuthenticator["authenticatorId"] = authenticatorType.authenticatorId
        selectedAuthenticator["params"] = authenticatorAuthParams.getParameterBodyAuthenticator()

        authBody["selectedAuthenticator"] = selectedAuthenticator

        val formBody: RequestBody = JsonUtil.getJsonObject(authBody).toString()
            .toRequestBody("application/json".toMediaTypeOrNull())

        val requestBuilder: Request.Builder = Request.Builder().url(authnUri)
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
        val authBody = LinkedHashMap<String, Any>()
        authBody["flowId"] = flowId

        val selectedAuthenticator = LinkedHashMap<String, String>()
        selectedAuthenticator["authenticatorId"] = authenticatorTypeId

        authBody["selectedAuthenticator"] = selectedAuthenticator;

        val formBody: RequestBody =  JsonUtil.getJsonObject(authBody).toString()
            .toRequestBody("application/json".toMediaTypeOrNull())

        val requestBuilder: Request.Builder = Request.Builder().url(authnUri)
        return requestBuilder.post(formBody).build()
    }
}