package io.wso2.android.api_authenticator.sdk.services.authorization_service

import com.fasterxml.jackson.databind.JsonNode
import io.wso2.android.api_authenticator.sdk.exceptions.AuthenticatorTypeException
import io.wso2.android.api_authenticator.sdk.exceptions.AuthorizationServiceException
import io.wso2.android.api_authenticator.sdk.exceptions.AuthorizeException
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.BasicAuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authenticator_type_factory.AuthenticatorTypeFactory
import io.wso2.android.api_authenticator.sdk.models.authorize_flow.AuthorizeFlow
import io.wso2.android.api_authenticator.sdk.models.http_client.http_client_builder.HttpClientBuilder
import io.wso2.android.api_authenticator.sdk.util.JsonUtil
import kotlinx.coroutines.CoroutineScope
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
 * Authenticator class to handle authentication.
 *
 * @property authorizationServiceConfig Configuration of the Authenticator `AuthorizationServiceConfig`
 */
class AuthorizationService private constructor(
    private val authorizationServiceConfig: AuthorizationServiceConfig
) {

    /**
     * OkHttpClient instance to handle network calls. If the trusted certificates are not provided,
     * create a less secure [OkHttpClient] instance, which bypasses the certificate validation.
     * `This is not recommended for production`.
     */
    private val client: OkHttpClient = HttpClientBuilder.getHttpClientInstance(
        authorizationServiceConfig.trustedCertificates
    )

    /**
     * Instance of the AuthenticatorBuilder that will be used throughout the application
     */
    private val authorizationServiceRequestBuilderInstance = AuthorizationServiceRequestBuilder()

    companion object {
        /**
         * Instance of the Authenticator that will be used throughout the application
         */
        private var authorizationServiceInstance = WeakReference<AuthorizationService?>(null)

        /**
         * Initialize the Authenticator instance and return the instance.
         *
         * @param authorizationServiceConfig Configuration of the Authenticator `AuthorizationServiceConfig`
         *
         * @return Initialized [AuthorizationService] instance
         */
        fun getInstance(
            authorizationServiceConfig: AuthorizationServiceConfig
        ): AuthorizationService {
            var authorizationService = authorizationServiceInstance.get()
            if (authorizationService == null) {
                authorizationService = AuthorizationService(authorizationServiceConfig)
                authorizationServiceInstance = WeakReference(authorizationService)
            }
            return authorizationService
        }

        /**
         * Get the Authenticator instance.
         * This method will return null if the Authenticator instance is not initialized.
         *
         * @return [AuthorizationService] instance
         *
         * @throws [AuthorizationServiceException] If the Authenticator instance is not initialized
         */
        fun getInstance(): AuthorizationService {
            return authorizationServiceInstance.get()
                ?: throw AuthorizationServiceException(AuthorizationServiceException.AUTHORIZATION_SERVICE_NOT_INITIALIZED)
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
    ): AuthorizeFlow = suspendCoroutine { continuation ->
        /**
         * Get full details of the all authenticators for the given flow.
         *
         * @param flowId Flow id of the authentication flow
         * @param authenticatorTypes List of authenticator types
         *
         * @return List of authenticator types with full details
         *
         * @throws AuthenticatorTypeException
         */
        suspend fun getDetailsOfAllAuthenticatorTypesGivenFlow(
            flowId: String,
            authenticatorTypes: ArrayList<AuthenticatorType>
        ): ArrayList<AuthenticatorType> = suspendCoroutine { outerContinuation ->

            /**
             * Get full details of the authenticator type.
             *
             * @param flowId Flow id of the authentication flow
             * @param authenticatorType Authenticator type that is required to get the full details
             *
             * @return Authenticator type with full details `AuthenticatorType`
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

                val request: Request = authorizationServiceRequestBuilderInstance
                    .getAuthenticatorTypeRequestBuilder(
                        authorizationServiceConfig.authnUri,
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
                                val authorizeFlow: AuthorizeFlow =
                                    AuthorizeFlow.fromJson(response.body!!.string())

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

        val coroutineScope = CoroutineScope(coroutineContext)

        coroutineScope.launch {
            val authorizeFlow: AuthorizeFlow = AuthorizeFlow.fromJson(responseBodyString)

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
     * Authorize the application.
     * This method will call the authorization endpoint and get the authenticators available for the
     * first step in the login flow.
     *
     * @throws [AuthorizeException] If the authorization fails
     */
    suspend fun authorize() {
        val request: Request = authorizationServiceRequestBuilderInstance.authorizeRequestBuilder(
            authorizationServiceConfig.authorizeUri,
            authorizationServiceConfig.clientId,
            authorizationServiceConfig.scope
        )

        client.newCall(request).enqueue(object : Callback {
            // Throw an `AuthorizeException` if the request fails
            override fun onFailure(call: Call, e: IOException) {
                val exception = AuthorizeException(e.message)
                throw exception
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    // reading the json from the response
                    val responseObject: JsonNode = JsonUtil.getJsonObject(response.body!!.string())


//                    handleAuthorizeFlow(context, model, AuthorizeFlowCallback(
//                        onSuccess = {
//                            onSuccessCallback(it)
//                        },
//                        onFailure = {
//                            onFailureCallback()
//                        }
//                    ))
                } catch (e: Exception) {
                    val exception = AuthorizeException(e.message)
                    throw exception
                }
            }
        })
    }
}
