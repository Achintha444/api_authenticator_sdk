package io.wso2.android.api_authenticator.sdk.core.managers.authenticator.impl

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
internal object AuthenticatorManagerImplRequestBuilder {

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

        authBody["selectedAuthenticator"] = selectedAuthenticator

        val formBody: RequestBody =  JsonUtil.getJsonObject(authBody).toString()
            .toRequestBody("application/json".toMediaTypeOrNull())

        val requestBuilder: Request.Builder = Request.Builder().url(authnUri)
        return requestBuilder.post(formBody).build()
    }
}