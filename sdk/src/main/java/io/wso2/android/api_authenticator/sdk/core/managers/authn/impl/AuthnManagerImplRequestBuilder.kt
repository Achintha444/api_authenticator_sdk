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
        redirectUri: String,
        scope: String,
        integrityToken: String? = null
    ): Request {
        val formBody: RequestBody = FormBody.Builder()
            .add("client_id", clientId)
            .add("redirect_uri", redirectUri)
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
        selectedAuthenticator["params"] = authenticatorAuthParams.getParameterBodyAuthenticator(
            authenticatorType.requiredParams!!
        )

        authBody["selectedAuthenticator"] = selectedAuthenticator

        val formBody: RequestBody = JsonUtil.getJsonObject(authBody).toString()
            .toRequestBody("application/json".toMediaTypeOrNull())

        val requestBuilder: Request.Builder = Request.Builder().url(authnUri)
        return requestBuilder.post(formBody).build()
    }

    /**
     * Build the request to logout the user.
     * This request will be used to logout the user from the application.
     *
     * @param logoutUri Logout endpoint
     * @param clientId Client id of the application
     * @param idToken Id token of the user
     *
     * @return [okhttp3.Request] to logout the user
     */
    internal fun logoutRequestBuilder(
        logoutUri: String,
        clientId: String,
        idToken: String
    ): Request {
        val formBody: RequestBody = FormBody.Builder()
            .add("response_mode", "direct")
            .add("client_id", clientId)
            .add("id_token_hint", idToken)
            .build()

        val requestBuilder: Request.Builder = Request.Builder().url(logoutUri)
        requestBuilder.addHeader("Accept", "application/json")
        requestBuilder.addHeader("Content-Type", "application/x-www-form-urlencoded")

        return requestBuilder.post(formBody).build()
    }
}