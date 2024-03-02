package io.wso2.android.api_authenticator.sdk.core.managers.authenticator.impl

import io.wso2.android.api_authenticator.sdk.core.managers.app_auth.impl.AppAuthManagerImpl
import io.wso2.android.api_authenticator.sdk.core.managers.authenticator.AuthenticatorManager
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.BasicAuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.authenticator_type_factory.AuthenticatorTypeFactory
import io.wso2.android.api_authenticator.sdk.models.authorize_flow.AuthorizeFlowNotSuccess
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticatorTypeException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
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
 * Authenticator manager implementation class
 * This class is responsible for handling the authenticator related operations.
 *
 * @property client The [OkHttpClient] instance.
 * @property authenticatorTypeFactory The [AuthenticatorTypeFactory] instance.
 * @property authenticatorManagerImplRequestBuilder The [AuthenticatorManagerImplRequestBuilder] instance.
 * @property authnUrl The authentication endpoint URL.
 */
internal class AuthenticatorManagerImpl(
    private val client: OkHttpClient,
    private val authenticatorTypeFactory: AuthenticatorTypeFactory,
    private val authenticatorManagerImplRequestBuilder: AuthenticatorManagerImplRequestBuilder,
    private val authnUrl: String
): AuthenticatorManager {
    companion object {
        /**
         * Instance of the [AuthenticatorManagerImpl] class.
         */
        private var authenticatorManagerImplInstance =
            WeakReference<AuthenticatorManagerImpl?>(null)

        /**
         * Initialize the [AuthenticatorManagerImpl] class.
         *
         * @property client The [OkHttpClient] instance.
         * @property authenticatorTypeFactory The [AuthenticatorTypeFactory] instance.
         * @property authenticatorManagerImplRequestBuilder The [AuthenticatorManagerImplRequestBuilder] instance.
         * @property authnUrl The authentication endpoint URL.
         *
         * @return The [AppAuthManagerImpl] instance.
         */
        fun getInstance(
            client: OkHttpClient,
            authenticatorTypeFactory: AuthenticatorTypeFactory,
            authenticatorManagerImplRequestBuilder: AuthenticatorManagerImplRequestBuilder,
            authnUrl: String
        ): AuthenticatorManagerImpl {
            var authenticatorManagerImpl = authenticatorManagerImplInstance.get()
            if (authenticatorManagerImpl == null) {
                authenticatorManagerImpl = AuthenticatorManagerImpl(
                    client,
                    authenticatorTypeFactory,
                    authenticatorManagerImplRequestBuilder,
                    authnUrl
                )
                authenticatorManagerImplInstance = WeakReference(authenticatorManagerImpl)
            }
            return authenticatorManagerImpl
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
    override suspend fun getDetailsOfAuthenticatorType(
        flowId: String,
        authenticatorType: AuthenticatorType
    ): AuthenticatorType = suspendCoroutine { continuation ->
        if (authenticatorType.authenticatorId == BasicAuthenticatorType.AUTHENTICATOR_TYPE) {
            val detailedAuthenticatorType: AuthenticatorType =
                authenticatorTypeFactory.getAuthenticatorType(
                    authenticatorType.authenticatorId,
                    authenticatorType.authenticator,
                    authenticatorType.idp,
                    authenticatorType.metadata,
                    authenticatorType.requiredParams
                )

            continuation.resume(detailedAuthenticatorType)
        } else {
            val request: Request = authenticatorManagerImplRequestBuilder
                .getAuthenticatorTypeRequestBuilder(
                    authnUrl,
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

                            if (authorizeFlow.nextStep.authenticators.size == 1) {
                                val detailedAuthenticatorType: AuthenticatorType =
                                    authenticatorTypeFactory.getAuthenticatorType(
                                        authorizeFlow.nextStep.authenticators[0].authenticatorId,
                                        authorizeFlow.nextStep.authenticators[0].authenticator,
                                        authorizeFlow.nextStep.authenticators[0].idp,
                                        authorizeFlow.nextStep.authenticators[0].metadata,
                                        authorizeFlow.nextStep.authenticators[0].requiredParams
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
    override suspend fun getDetailsOfAllAuthenticatorTypesGivenFlow(
        flowId: String,
        authenticatorTypes: ArrayList<AuthenticatorType>
    ): ArrayList<AuthenticatorType> = suspendCoroutine { outerContinuation ->

        /**
         * If there is only one authenticator type, do not call the endpoint to get the details.
         * Because the details are already available, just calling the AuthenticatorTypeFactory to
         * set the correct authenticator type.
         */
        /**
         * If there is only one authenticator type, do not call the endpoint to get the details.
         * Because the details are already available, just calling the AuthenticatorTypeFactory to
         * set the correct authenticator type.
         */
        if (authenticatorTypes.size == 1) {
            val detailedAuthenticatorType: AuthenticatorType =
                authenticatorTypeFactory.getAuthenticatorType(
                    authenticatorTypes[0].authenticatorId,
                    authenticatorTypes[0].authenticator,
                    authenticatorTypes[0].idp,
                    authenticatorTypes[0].metadata,
                    authenticatorTypes[0].requiredParams
                )
            authenticatorTypes[0] = detailedAuthenticatorType

            outerContinuation.resume(authenticatorTypes)
        } else {
            val coroutineScope = CoroutineScope(GlobalScope.coroutineContext)
            coroutineScope.launch {

                // Authenticator types with full details
                val detailedAuthenticatorTypes: ArrayList<AuthenticatorType> = ArrayList()

                for (authenticatorType in authenticatorTypes) {
                    try {
                        runCatching {
                            getDetailsOfAuthenticatorType(
                                flowId,
                                authenticatorType
                            )
                        }
                            .onSuccess {
                                detailedAuthenticatorTypes.add(it)

                                if (detailedAuthenticatorTypes.size == authenticatorTypes.size) {
                                    outerContinuation.resume(detailedAuthenticatorTypes)
                                }
                            }
                            .onFailure {
                                outerContinuation.resumeWithException(it)
                            }
                    } catch (e: AuthenticatorTypeException) {
                        outerContinuation.resumeWithException(e)
                        break
                    }
                }
            }
        }
    }
}