package io.wso2.android.api_authenticator.sdk.core.managers.flow.impl

import com.fasterxml.jackson.databind.JsonNode
import io.wso2.android.api_authenticator.sdk.core.managers.authenticator.AuthenticatorManager
import io.wso2.android.api_authenticator.sdk.core.managers.flow.FlowManager
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowSuccess
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticatorTypeException
import io.wso2.android.api_authenticator.sdk.models.exceptions.FlowManagerException
import io.wso2.android.api_authenticator.sdk.models.flow_status.FlowStatus
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class FlowManagerImpl(
    private val authenticatorManager: AuthenticatorManager
) : FlowManager {
    companion object {
        /**
         * Instance of the [FlowManagerImpl] that will be used throughout the application
         */
        private var flowManagerImplInstance: WeakReference<FlowManagerImpl> = WeakReference(null)

        /**
         * Initialize the [FlowManagerImpl] instance and return the instance.
         *
         * @param authenticatorManager The [AuthenticatorManager] instance
         */
        fun getInstance(
            authenticatorManager: AuthenticatorManager
        ): FlowManagerImpl {
            var flowManagerImpl = flowManagerImplInstance.get()
            if (flowManagerImpl == null) {
                flowManagerImpl = FlowManagerImpl(authenticatorManager)
                flowManagerImplInstance = WeakReference(flowManagerImpl)
            }
            return flowManagerImpl
        }
    }

    /**
     * Flow id of the authorization flow
     */
    private lateinit var flowId: String

    /**
     * Set the flow id of the authorization flow
     *
     * @param flowId Flow id of the authorization flow
     *
     */
    override fun setFlowId(flowId: String) {
        this.flowId = flowId
    }

    /**
     * Get the flow id of the authorization flow
     *
     * @return Flow id of the authorization flow
     */
    override fun getFlowId(): String {
        return flowId
    }

    /**
     * Handle the authorization flow and return the authenticator types in the next step.
     *
     * @param responseBodyString Response body string of the authorization request
     *
     * @return [AuthenticationFlow] with the authenticator types in the next step
     *
     * @throws [AuthenticatorTypeException]
     */
    private suspend fun handleAuthorizeFlow(
        responseBodyString: String
    ): AuthenticationFlowNotSuccess = suspendCoroutine { continuation ->
        runBlocking {
            val authenticationFlow: AuthenticationFlowNotSuccess = AuthenticationFlowNotSuccess.fromJson(
                responseBodyString
            )

            runCatching {
                authenticatorManager.getDetailsOfAllAuthenticatorTypesGivenFlow(
                    authenticationFlow.flowId,
                    authenticationFlow.nextStep.authenticators
                )
            }.onSuccess {
                authenticationFlow.nextStep.authenticators = it
                continuation.resume(authenticationFlow)
            }.onFailure {
                continuation.resumeWithException(it)
            }
        }
    }

    /**
     * Manage the state of the authorization flow.
     * This function will return the [AuthenticationFlowNotSuccess] if the flow is incomplete.
     * This function will return the [AuthenticationFlowSuccess] if the flow is completed.
     *
     * @param responseObject Response object of the authorization request
     *
     * @return [AuthenticationFlow] with the authenticator types in the next step
     * @throws [AuthenticatorTypeException] If the flow is failed
     * @throws [FlowManagerException] If the flow is failed incomplete
     *
     * TODO: Need to check additional check to flowid to check if the flow is the same as the current flow
     */
    override suspend fun manageStateOfAuthorizeFlow(
        responseObject: JsonNode
    ): AuthenticationFlow? = suspendCoroutine { continuation ->
        when (responseObject.get("flowStatus").asText()) {
            FlowStatus.FAIL_INCOMPLETE.flowStatus -> {
                val exception = FlowManagerException(
                    FlowManagerException.AUTHENTICATION_NOT_COMPLETED
                )
                continuation.resumeWithException(exception)
            }

            FlowStatus.INCOMPLETE.flowStatus -> {
                runBlocking {
                    runCatching {
                        handleAuthorizeFlow(responseObject.toString())
                    }.onSuccess {
                        continuation.resume(it)
                    }.onFailure {
                        continuation.resumeWithException(it)
                    }
                }
            }

            FlowStatus.SUCCESS.flowStatus -> {
                continuation.resume(
                    AuthenticationFlowSuccess.fromJson(responseObject.toString())
                )
            }
        }
    }
}


