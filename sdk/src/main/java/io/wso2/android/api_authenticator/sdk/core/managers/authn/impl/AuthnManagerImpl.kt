package io.wso2.android.api_authenticator.sdk.core.managers.authn.impl

import com.fasterxml.jackson.databind.JsonNode
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.managers.authn.AuthnManager
import io.wso2.android.api_authenticator.sdk.core.managers.flow.FlowManager
import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticatorTypeException
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthnManagerException
import io.wso2.android.api_authenticator.sdk.util.JsonUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.ref.WeakReference
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * AuthnManagerImpl class is the implementation of the [AuthnManager] interface.
 * Which is used to handle the initial authorization and the authentication flow.
 *
 * @property authenticationCoreConfig Configuration of the Identity Server [AuthenticationCoreConfig]
 * @property client OkHttpClient instance to handle network calls [OkHttpClient]
 * @property authenticationCoreRequestBuilder Request builder class to build the requests [AuthnManagerImplRequestBuilder]
 *
 * TODO: Dispose managers instances when the authentication flow is completed.
 */
internal class AuthnManagerImpl private constructor(
    private val authenticationCoreConfig: AuthenticationCoreConfig,
    private val client: OkHttpClient,
    private val authenticationCoreRequestBuilder: AuthnManagerImplRequestBuilder,
    private val flowManager: FlowManager
) : AuthnManager {
    companion object {
        /**
         * Instance of the [AuthnManagerImpl] that will be used throughout the application
         */
        private var authnManagerImplInstance: WeakReference<AuthnManagerImpl> = WeakReference(null)

        /**
         * Initialize the [AuthnManagerImpl] instance and return the instance.
         *
         * @param authenticationCoreConfig Configuration of the Authenticator [AuthenticationCoreConfig]
         * @param client OkHttpClient instance to handle network calls
         * @param authenticationCoreRequestBuilder Request builder class to build the requests
         * @param flowManager Flow manager instance to manage the state of the authorization flow
         *
         * @return Initialized [AuthnManagerImpl] instance
         */
        fun getInstance(
            authenticationCoreConfig: AuthenticationCoreConfig,
            client: OkHttpClient,
            authenticationCoreRequestBuilder: AuthnManagerImplRequestBuilder,
            flowManager: FlowManager
        ): AuthnManagerImpl {
            var authnManagerImpl = authnManagerImplInstance.get()
            if (authnManagerImpl == null) {
                authnManagerImpl = AuthnManagerImpl(
                    authenticationCoreConfig,
                    client,
                    authenticationCoreRequestBuilder,
                    flowManager
                )
                authnManagerImplInstance = WeakReference(authnManagerImpl)
            }
            return authnManagerImpl
        }
    }

    /**
     * Authorize the application.
     * This method will call the authorization endpoint and get the authenticators available for the
     * first step in the authentication flow.
     *
     * @throws [AuthnManagerException] If the authorization fails
     * @throws [IOException] If the request fails due to a network error
     */
    override suspend fun authorize(): AuthenticationFlow? = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            val request: Request = authenticationCoreRequestBuilder.authorizeRequestBuilder(
                authenticationCoreConfig.getAuthorizeUrl(),
                authenticationCoreConfig.getClientId(),
                authenticationCoreConfig.getRedirectUri(),
                authenticationCoreConfig.getScope(),
                authenticationCoreConfig.getIntegrityToken()
            )

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                @Throws(IOException::class, AuthnManagerException::class)
                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.code == 200) {
                            // reading the json from the response
                            val responseObject: JsonNode =
                                JsonUtil.getJsonObject(response.body!!.string())

                            // set the flow id of the authorization flow
                            flowManager.setFlowId(responseObject.get("flowId").asText())

                            CoroutineScope(Dispatchers.IO).launch {
                                runCatching {
                                    flowManager.manageStateOfAuthorizeFlow(responseObject)
                                }.onSuccess {
                                    continuation.resume(it)
                                }.onFailure {
                                    continuation.resumeWithException(it)
                                }
                            }
                        } else {
                            // throw an `AuthnManagerException` if the request does not return 200
                            continuation.resumeWithException(
                                AuthnManagerException(
                                    response.message
                                )
                            )
                        }
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
            })
        }
    }

    /**
     * Send the authentication parameters to the authentication endpoint and get the next step of the
     * authentication flow. If the authentication flow has only one step, this method will return
     * the success response of the authentication flow if the authentication is successful.
     *
     * @param authenticatorType Authenticator type of the selected authenticator
     * @param authenticatorParameters Authenticator parameters of the selected authenticator
     *
     * @throws [AuthnManagerException] If the authentication fails
     * @throws [AuthenticatorTypeException] If the authenticator type is not valid
     * @throws [FlowManagerException] If the flow is incomplete
     * @throws [IOException] If the request fails due to a network error
     *
     * @return [AuthenticationFlow] with the next step of the authentication flow
     *
     * TODO: In the AuthnManager class we can use retrofit to make the network calls.
     */
    override suspend fun authenticate(
        authenticatorType: AuthenticatorType,
        authenticatorParameters: AuthParams
    ): AuthenticationFlow? = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            val request: Request = authenticationCoreRequestBuilder.authenticateRequestBuilder(
                authenticationCoreConfig.getAuthnUrl(),
                flowManager.getFlowId(),
                authenticatorType,
                authenticatorParameters
            )

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                @Throws(
                    IOException::class,
                    AuthnManagerException::class,
                    AuthenticatorTypeException::class
                )
                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.code == 200) {
                            // reading the json from the response
                            val responseObject: JsonNode =
                                JsonUtil.getJsonObject(response.body!!.string())

                            CoroutineScope(Dispatchers.IO).launch {
                                runCatching {
                                    flowManager.manageStateOfAuthorizeFlow(responseObject)
                                }.onSuccess {
                                    continuation.resume(it)
                                }.onFailure {
                                    continuation.resumeWithException(it)
                                }
                            }
                        } else {
                            // Throw an [AuthnManagerException] if the request does not return 200 response.message
                            continuation.resumeWithException(
                                AuthnManagerException(
                                    response.message
                                )
                            )
                        }
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
            })
        }
    }

    /**
     * Logout the user from the application.
     *
     * @param idToken Id token of the user
     *
     * @throws [AuthnManagerException] If the logout fails
     * @throws [IOException] If the request fails due to a network error
     */
    suspend fun logout(
        idToken: String
    ): Unit? = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            val request: Request = authenticationCoreRequestBuilder.logoutRequestBuilder(
                authenticationCoreConfig.getLogoutUrl(),
                idToken
            )

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (response.code != 200) {
                            // Throw an [AuthnManagerException] if the request does not return 200 response.message
                            continuation.resumeWithException(
                                AuthnManagerException(
                                    response.message
                                )
                            )
                        }
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                }
            })
        }
    }
}
