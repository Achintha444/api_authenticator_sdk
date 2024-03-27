package io.wso2.android.api_authenticator.sdk.sample.data.impl.repository

import com.fasterxml.jackson.databind.JsonNode
import io.wso2.android.api_authenticator.sdk.sample.domain.model.UserDetails
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.UserRepository
import io.wso2.android.api_authenticator.sdk.sample.util.Config
import io.wso2.android.api_authenticator.sdk.sample.util.JsonUtil
import io.wso2.android.api_authenticator.sdk.sample.util.LessSecureHttpClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl @Inject constructor() : UserRepository {

    private val lessSecureHttpClient = LessSecureHttpClient.getInstance()

    override suspend fun getUserDetails(accessToken: String): UserDetails? =
        suspendCoroutine { continuation ->
            val requestBuilder: Request.Builder = Request.Builder().url(
                Config.getBaseUrl() + "/scim2/Me"
            )
            requestBuilder.addHeader("Accept", "application/json")
            requestBuilder.addHeader("Authorization", "Bearer $accessToken")

            val request: Request = requestBuilder.get().build()

            lessSecureHttpClient.getClient().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.code == 200) {
                            // reading the json from the response
                            val responseObject: JsonNode =
                                JsonUtil.getJsonObject(response.body!!.string())
                            continuation.resume(UserDetails(
                                username = responseObject.get("userName").asText(),
                                firstName = responseObject.get("name")?.get("givenName")?.asText(),
                                lastName = responseObject.get("name")?.get("familyName")?.asText()
                            ))
                        } else {
                            continuation.resumeWithException(
                                Error("Failed to get user details")
                            )
                        }
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
            })
        }
}
