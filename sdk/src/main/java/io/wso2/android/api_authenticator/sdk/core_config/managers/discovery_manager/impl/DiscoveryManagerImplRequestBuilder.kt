package io.wso2.android.api_authenticator.sdk.core_config.managers.discovery_manager.impl

import io.wso2.android.api_authenticator.sdk.models.autheniticator.Authenticator
import io.wso2.android.api_authenticator.sdk.util.JsonUtil
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Builder function related to the [DiscoveryManagerImpl]
 */
object DiscoveryManagerImplRequestBuilder {

    /**
     * Build the request to call the discovery endpoint.
     *
     * @param discoveryUri Discovery endpoint
     *
     * @return [okhttp3.Request] to call the discovery endpoint
     */
    internal fun discoveryRequestBuilder(discoveryUri: String): Request {
        val requestBuilder: Request.Builder = Request.Builder().url(discoveryUri)
        requestBuilder.addHeader("Accept", "application/json")

        return requestBuilder.get().build()
    }
}
