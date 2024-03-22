package io.wso2.android.api_authenticator.sdk.core.managers.authenticator.impl

import io.wso2.android.api_authenticator.sdk.core.managers.app_auth.impl.AppAuthManagerImpl
import io.wso2.android.api_authenticator.sdk.core.managers.authenticator.AuthenticatorManager
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorTypes
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.authenticator_type_factory.AuthenticatorTypeFactory
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticatorTypeException
import kotlinx.coroutines.Dispatchers
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
 * Authenticator manager implementation class
 * This class is responsible for handling the authenticator related operations.
 *
 * @property client The [OkHttpClient] instance.
 * @property authenticatorTypeFactory The [AuthenticatorTypeFactory] instance.
 * @property authenticatorManagerImplRequestBuilder The [AuthenticatorManagerImplRequestBuilder] instance.
 * @property authnUrl The authentication endpoint URL.
 */
internal class AuthenticatorManagerImpl private constructor(
    private val client: OkHttpClient,
    private val authenticatorTypeFactory: AuthenticatorTypeFactory,
    private val authenticatorManagerImplRequestBuilder: AuthenticatorManagerImplRequestBuilder,
    private val authnUrl: String
) : AuthenticatorManager {
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
    ): AuthenticatorType = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            if (authenticatorType.authenticator == AuthenticatorTypes.BASIC_AUTHENTICATOR.authenticatorType) {
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
                        val exception =
                            AuthenticatorTypeException(
                                e.message,
                                authenticatorType.authenticator
                            )
                        continuation.resumeWithException(exception)
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        try {
                            if (response.code == 200) {
                                val authenticationFlow: AuthenticationFlowNotSuccess =
                                    AuthenticationFlowNotSuccess.fromJson(
                                        response.body!!.string()
                                    )

                                if (authenticationFlow.nextStep.authenticators.size == 1) {
                                    val detailedAuthenticatorType: AuthenticatorType =
                                        authenticatorTypeFactory.getAuthenticatorType(
                                            authenticationFlow.nextStep.authenticators[0].authenticatorId,
                                            authenticationFlow.nextStep.authenticators[0].authenticator,
                                            authenticationFlow.nextStep.authenticators[0].idp,
                                            authenticationFlow.nextStep.authenticators[0].metadata,
                                            authenticationFlow.nextStep.authenticators[0].requiredParams
                                        )

                                    continuation.resume(detailedAuthenticatorType)
                                } else {
                                    val exception =
                                        AuthenticatorTypeException(
                                            AuthenticatorTypeException.AUTHENTICATOR_NOT_FOUND_OR_MORE_THAN_ONE,
                                            authenticatorType.authenticator
                                        )
                                    continuation.resumeWithException(exception)
                                }
                            } else {
                                // Throw an `AuthenticatorTypeException` if the request does not return 200
                                val exception =
                                    AuthenticatorTypeException(
                                        response.message,
                                        authenticatorType.authenticator,
                                        response.code.toString()
                                    )
                                continuation.resumeWithException(exception)
                            }
                        } catch (e: IOException) {
                            val exception =
                                AuthenticatorTypeException(
                                    e.message,
                                    authenticatorType.authenticator
                                )
                            continuation.resumeWithException(exception)
                        }
                    }
                })
            }
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
    ): ArrayList<AuthenticatorType> {
        if (authenticatorTypes.size == 1) {
            // If there is only one authenticator type, don't call the endpoint
            val detailedAuthenticatorType = authenticatorTypeFactory.getAuthenticatorType(
                authenticatorTypes[0].authenticatorId,
                authenticatorTypes[0].authenticator,
                authenticatorTypes[0].idp,
                authenticatorTypes[0].metadata,
                authenticatorTypes[0].requiredParams
            )
            return arrayListOf(detailedAuthenticatorType)
        } else {
            // Fetch details for multiple authenticator types
            val detailedAuthenticatorTypes = authenticatorTypes.map { authenticatorType ->
                getDetailsOfAuthenticatorType(flowId, authenticatorType)
            }
            return detailedAuthenticatorTypes.toCollection(ArrayList())
        }
    }

    /**
     * Remove the instance of the [AuthenticatorManagerImpl]
     */
    override fun dispose() {
        authenticatorManagerImplInstance.clear()
    }
}