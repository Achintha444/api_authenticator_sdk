package io.wso2.android.api_authenticator.sdk.core.managers.flow.impl

import com.fasterxml.jackson.databind.JsonNode
import io.wso2.android.api_authenticator.sdk.core.managers.authenticator.AuthenticatorManager
import io.wso2.android.api_authenticator.sdk.core.managers.flow.FlowManager
import io.wso2.android.api_authenticator.sdk.models.authorize_flow.AuthorizeFlow
import io.wso2.android.api_authenticator.sdk.models.authorize_flow.AuthorizeFlowNotSuccess
import io.wso2.android.api_authenticator.sdk.models.authorize_flow.AuthorizeFlowSuccess
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticatorTypeException
import io.wso2.android.api_authenticator.sdk.models.exceptions.FlowManagerException
import io.wso2.android.api_authenticator.sdk.models.flow_status.FlowStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class FlowManagerImpl(
    private val authenticatorManager: AuthenticatorManager
): FlowManager {

    private val coroutineScope = CoroutineScope(GlobalScope.coroutineContext)

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
     * @return [AuthorizeFlow] with the authenticator types in the next step
     *
     * @throws [AuthenticatorTypeException]
     */
    private suspend fun handleAuthorizeFlow(
        responseBodyString: String
    ): AuthorizeFlowNotSuccess = suspendCoroutine { continuation ->
        coroutineScope.launch {
            val authorizeFlow: AuthorizeFlowNotSuccess = AuthorizeFlowNotSuccess.fromJson(
                responseBodyString
            )

            try {
                authenticatorManager.getDetailsOfAllAuthenticatorTypesGivenFlow(
                    authorizeFlow.flowId,
                    authorizeFlow.nextStep.authenticators
                )?.let {
                    authorizeFlow.nextStep.authenticators = it

                    continuation.resume(authorizeFlow)
                }
            } catch (e: AuthenticatorTypeException) {
                continuation.resumeWithException(e)
            }
        }
    }

    /**
     * Manage the state of the authorization flow.
     * This function will return the [AuthorizeFlowNotSuccess] if the flow is incomplete.
     * This function will return the [AuthorizeFlowSuccess] if the flow is completed.
     *
     * @param responseObject Response object of the authorization request
     *
     * @return [AuthorizeFlow] with the authenticator types in the next step
     * @throws [AuthenticatorTypeException] If the flow is failed
     * @throws [FlowManagerException] If the flow is failed incomplete
     *
     * TODO: Need to check additional check to flowid to check if the flow is the same as the current flow
     */
    override suspend fun manageStateOfAuthorizeFlow(
        responseObject: JsonNode
    ): AuthorizeFlow? = suspendCoroutine { continuation ->
        when (responseObject.get("flowStatus").asText()) {
            FlowStatus.FAIL_INCOMPLETE.flowStatus -> {
                val exception = FlowManagerException(
                    FlowManagerException.AUTHENTICATION_NOT_COMPLETED
                )
                continuation.resumeWithException(exception)
            }

            FlowStatus.INCOMPLETE.flowStatus -> {
                coroutineScope.launch {
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
    }
}


