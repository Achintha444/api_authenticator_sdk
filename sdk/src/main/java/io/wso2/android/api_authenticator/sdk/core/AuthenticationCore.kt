package io.wso2.android.api_authenticator.sdk.core

import android.content.Context
import com.fasterxml.jackson.databind.JsonNode
import io.wso2.android.api_authenticator.sdk.exceptions.AuthenticationCoreException
import io.wso2.android.api_authenticator.sdk.exceptions.AuthenticatorTypeException
import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.BasicAuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authenticator_type_factory.AuthenticatorTypeFactory
import io.wso2.android.api_authenticator.sdk.models.authorize_flow.AuthorizeFlow
import io.wso2.android.api_authenticator.sdk.models.authorize_flow.AuthorizeFlowNotSuccess
import io.wso2.android.api_authenticator.sdk.models.authorize_flow.AuthorizeFlowSuccess
import io.wso2.android.api_authenticator.sdk.models.flow_status.FlowStatus
import io.wso2.android.api_authenticator.sdk.models.http_client.http_client_builder.HttpClientBuilder
import io.wso2.android.api_authenticator.sdk.util.AppAuthManager
import io.wso2.android.api_authenticator.sdk.util.JsonUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.launch
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
 * Authentication core class which has the core functionality of the Authenticator SDK.
 *
 * @property authenticationCoreConfig Configuration of the [AuthenticationCore]. [AuthenticationCoreConfig]
 */
class AuthenticationCore private constructor(
    private val authenticationCoreConfig: AuthenticationCoreConfig
) {

    /**
     * OkHttpClient instance to handle network calls. If the trusted certificates are not provided,
     * create a less secure [OkHttpClient] instance, which bypasses the certificate validation.
     * `This is not recommended for production`.
     */
    private val client: OkHttpClient = HttpClientBuilder.getHttpClientInstance(
        authenticationCoreConfig.getTrustedCertificates()
    )

    /**
     * Instance of the [AuthenticationCoreRequestBuilder] that will be used throughout the application
     */
    private val authenticationCoreRequestBuilderInstance = AuthenticationCoreRequestBuilder()

    /**
     * Instance of the [AppAuthManager] that will be used throughout the application
     * to handle the AppAuth SDK.
     */
    private val appAuthManagerInstance = AppAuthManager.getInstance(
        client,
        authenticationCoreConfig.getClientId(),
        authenticationCoreConfig.getTokenUrl(),
        authenticationCoreConfig.getAuthorizeUrl()
    )

    companion object {
        /**
         * Instance of the [AuthenticationCore] that will be used throughout the application
         */
        private var authenticationCoreInstance = WeakReference<AuthenticationCore?>(null)

        /**
         * Initialize the AuthenticationCore instance and return the instance.
         *
         * @param authenticationCoreConfig Configuration of the Authenticator [AuthenticationCoreConfig]
         *
         * @return Initialized [AuthenticationCore] instance
         */
        fun getInstance(
            authenticationCoreConfig: AuthenticationCoreConfig
        ): AuthenticationCore {
            var authenticationCore = authenticationCoreInstance.get()
            if (authenticationCore == null) {
                authenticationCore = AuthenticationCore(authenticationCoreConfig)
                authenticationCoreInstance = WeakReference(authenticationCore)
            }
            return authenticationCore
        }

        /**
         * Get the AuthenticationCore instance.
         * This method will return null if the AuthenticationCore instance is not initialized.
         *
         * @return [AuthenticationCore] instance
         *
         * @throws [AuthenticationCoreException] If the AuthenticationCore instance is not initialized
         */
        fun getInstance(): AuthenticationCore {
            return authenticationCoreInstance.get()
                ?: throw AuthenticationCoreException(AuthenticationCoreException.AUTHORIZATION_SERVICE_NOT_INITIALIZED)
        }
    }

    /**
     * Handle the authorization flow and return the authenticator types in the next step.
     *
     * @param responseBodyString Response body string of the authorization request
     *
     * @return [AuthorizeFlow] with the authenticator types in the next step
     *
     * @throws [AuthenticatorTypeException]
     */
    private suspend fun handleAuthorizeFlow(
        responseBodyString: String
    ): AuthorizeFlowNotSuccess = suspendCoroutine { continuation ->

        val coroutineScope = CoroutineScope(coroutineContext)

        coroutineScope.launch {
            val authorizeFlow: AuthorizeFlowNotSuccess = AuthorizeFlowNotSuccess.fromJson(
                responseBodyString
            )

            try {
                getDetailsOfAllAuthenticatorTypesGivenFlow(
                    authorizeFlow.flowId,
                    authorizeFlow.nextStep.authenticatorTypes
                )?.let {
                    authorizeFlow.nextStep.authenticatorTypes = it

                    continuation.resume(authorizeFlow)
                }
            } catch (e: AuthenticatorTypeException) {
                continuation.resumeWithException(e)
            }
        }
    }

    /**
     * Get full details of the authenticator type.
     *
     * @param flowId Flow id of the authentication flow
     * @param authenticatorType Authenticator type that is required to get the full details
     *
     * @return Authenticator type with full details [AuthenticatorType]
     *
     * @throws AuthenticatorTypeException
     */
    suspend fun getDetailsOfAuthenticatorType(
        flowId: String,
        authenticatorType: AuthenticatorType
    ): AuthenticatorType = suspendCoroutine { continuation ->
        if (authenticatorType.authenticatorId == BasicAuthenticatorType.AUTHENTICATOR_TYPE) {
            val detailedAuthenticatorType: AuthenticatorType =
                AuthenticatorTypeFactory.getAuthenticatorType(
                    authenticatorType.authenticatorId,
                    authenticatorType.authenticator,
                    authenticatorType.idp,
                    authenticatorType.metadata,
                    authenticatorType.requiredParams
                )

            continuation.resume(detailedAuthenticatorType)
        }

        val request: Request = authenticationCoreRequestBuilderInstance
            .getAuthenticatorTypeRequestBuilder(
                authenticationCoreConfig.getAuthnUrl(),
                flowId,
                authenticatorType.authenticatorId
            )

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val exception = AuthenticatorTypeException(
                    e.message,
                    authenticatorType.authenticator
                )
                continuation.resumeWithException(exception)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.code == 200) {
                        val authorizeFlow: AuthorizeFlowNotSuccess =
                            AuthorizeFlowNotSuccess.fromJson(response.body!!.string())

                        if (authorizeFlow.nextStep.authenticatorTypes.size == 1) {
                            val detailedAuthenticatorType: AuthenticatorType =
                                AuthenticatorTypeFactory.getAuthenticatorType(
                                    authorizeFlow.nextStep.authenticatorTypes[0].authenticatorId,
                                    authorizeFlow.nextStep.authenticatorTypes[0].authenticator,
                                    authorizeFlow.nextStep.authenticatorTypes[0].idp,
                                    authorizeFlow.nextStep.authenticatorTypes[0].metadata,
                                    authorizeFlow.nextStep.authenticatorTypes[0].requiredParams
                                )

                            continuation.resume(detailedAuthenticatorType)
                        } else {
                            val exception = AuthenticatorTypeException(
                                AuthenticatorTypeException.AUTHENTICATOR_NOT_FOUND_OR_MORE_THAN_ONE,
                                authenticatorType.authenticator
                            )
                            continuation.resumeWithException(exception)
                        }
                    } else {
                        // Throw an `AuthenticatorTypeException` if the request does not return 200
                        val exception = AuthenticatorTypeException(
                            response.message,
                            authenticatorType.authenticator,
                            response.code.toString()
                        )
                        continuation.resumeWithException(exception)
                    }
                } catch (e: IOException) {
                    val exception = AuthenticatorTypeException(
                        e.message,
                        authenticatorType.authenticator
                    )
                    continuation.resumeWithException(exception)
                }
            }
        })
    }

    /**
     * Get full details of the all authenticators for the given flow.
     *
     * @param flowId Flow id of the authentication flow
     * @param authenticatorTypes List of authenticator types
     *
     * @return List of authenticator types with full details [ArrayList<AuthenticatorType>]
     *
     * @throws AuthenticatorTypeException
     */
    suspend fun getDetailsOfAllAuthenticatorTypesGivenFlow(
        flowId: String,
        authenticatorTypes: ArrayList<AuthenticatorType>
    ): ArrayList<AuthenticatorType> = suspendCoroutine { outerContinuation ->

        /**
         * If there is only one authenticator type, do not call the endpoint to get the details.
         * Because the details are already available, just calling the AuthenticatorTypeFactory to
         * set the correct authenticator type.
         */
        if (authenticatorTypes.size == 1) {
            val detailedAuthenticatorType: AuthenticatorType =
                AuthenticatorTypeFactory.getAuthenticatorType(
                    authenticatorTypes[0].authenticatorId,
                    authenticatorTypes[0].authenticator,
                    authenticatorTypes[0].idp,
                    authenticatorTypes[0].metadata,
                    authenticatorTypes[0].requiredParams
                )
            authenticatorTypes[0] = detailedAuthenticatorType

            outerContinuation.resume(authenticatorTypes)
        } else {
            val coroutineScope = CoroutineScope(coroutineContext)
            coroutineScope.launch {

                // Authenticator types with full details
                val detailedAuthenticatorTypes: ArrayList<AuthenticatorType> = ArrayList()

                for (authenticatorType in authenticatorTypes) {
                    try {
                        getDetailsOfAuthenticatorType(
                            flowId,
                            authenticatorType
                        )?.let {
                            detailedAuthenticatorTypes.add(it)

                            if (detailedAuthenticatorTypes.size == authenticatorTypes.size) {
                                outerContinuation.resume(detailedAuthenticatorTypes)
                            }
                        }
                    } catch (e: AuthenticatorTypeException) {
                        outerContinuation.resumeWithException(e)
                        break
                    }
                }
            }
        }
    }

    /**
     * Authorize the application.
     * This method will call the authorization endpoint and get the authenticators available for the
     * first step in the authentication flow.
     *
     * @throws [AuthenticationCoreException] If the authorization fails
     * @throws [IOException] If the request fails due to a network error
     */
    suspend fun authorize(): AuthorizeFlow? = suspendCoroutine { continuation ->
        val request: Request = authenticationCoreRequestBuilderInstance.authorizeRequestBuilder(
            authenticationCoreConfig.getAuthorizeUrl(),
            authenticationCoreConfig.getClientId(),
            authenticationCoreConfig.getScope(),
            authenticationCoreConfig.getIntegrityToken()
        )

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.code == 200) {
                        // reading the json from the response
                        val responseObject: JsonNode =
                            JsonUtil.getJsonObject(response.body!!.string())

                        GlobalScope.launch(Dispatchers.Default) {
                            handleAuthorizeFlow(responseObject.toString())?.let {
                                continuation.resume(it)
                            }
                        }
                    } else {
                        // throw an `AuthenticationCoreException` if the request does not return 200
                        val exception = AuthenticationCoreException(response.message)
                        continuation.resumeWithException(exception)
                    }
                } catch (e: Exception) {
                    val exception = AuthenticationCoreException(e.message)
                    continuation.resumeWithException(exception)
                }
            }
        })
    }

    /**
     * Send the authentication parameters to the authentication endpoint and get the next step of the
     * authentication flow. If the authentication flow has only one step, this method will return
     * the success response of the authentication flow if the authentication is successful.
     *
     * @param flowId Flow id of the authentication flow
     * @param authenticatorType Authenticator type of the selected authenticator
     * @param authenticatorParameters Authenticator parameters of the selected authenticator
     *
     * @throws [AuthenticationCoreException] If the authentication fails
     * @throws [IOException] If the request fails due to a network error
     *
     * @return [AuthorizeFlow] with the next step of the authentication flow
     */
    suspend fun authenticate(
        flowId: String,
        authenticatorType: AuthenticatorType,
        authenticatorParameters: AuthParams,
    ): AuthorizeFlow? = suspendCoroutine { continuation ->
        val request: Request = authenticationCoreRequestBuilderInstance.authenticateRequestBuilder(
            authenticationCoreConfig.getAuthnUrl(),
            flowId,
            authenticatorType,
            authenticatorParameters
        )

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.code == 200) {
                        // reading the json from the response
                        val responseObject: JsonNode =
                            JsonUtil.getJsonObject(response.body!!.string())

                        // assessing the flow status
                        when (responseObject["flowStatus"] as String) {
                            FlowStatus.FAIL_INCOMPLETE.flowStatus -> {
                                val exception = AuthenticationCoreException(
                                    AuthenticationCoreException.AUTHENTICATION_NOT_COMPLETED
                                )
                                continuation.resumeWithException(exception)
                            }

                            FlowStatus.INCOMPLETE.flowStatus -> {
                                GlobalScope.launch(Dispatchers.Default) {
                                    handleAuthorizeFlow(responseObject.toString())?.let {
                                        continuation.resume(it)
                                    }
                                }
                            }

                            FlowStatus.SUCCESS.flowStatus -> {
                                continuation.resume(
                                    AuthorizeFlowSuccess.fromJson(responseObject.toString())
                                )
                            }
                        }
                    } else {
                        // Throw an `AuthenticationCoreException` if the request does not return 200
                        val exception = AuthenticationCoreException(response.message)
                        continuation.resumeWithException(exception)
                    }
                } catch (e: IOException) {
                    continuation.resumeWithException(e)
                }
            }
        })
    }

    /**
     * Get the access token using the authorization code.
     *
     * @param context Context of the application
     * @param authorizationCode Authorization code
     *
     * @return Access token [String]
     */
    suspend fun getAccessToken(
        context: Context,
        authorizationCode: String
    ): String? = suspendCoroutine { continuation ->
        GlobalScope.launch(Dispatchers.Default) {
            appAuthManagerInstance.exchangeAuthorizationCodeForAccessToken(
                authorizationCode,
                context
            )?.let {
                continuation.resume(it)
            }
        }
    }

}
