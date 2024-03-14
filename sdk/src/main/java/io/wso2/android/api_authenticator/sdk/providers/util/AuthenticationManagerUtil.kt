package io.wso2.android.api_authenticator.sdk.providers.util

import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow
import io.wso2.android.api_authenticator.sdk.models.flow_status.FlowStatus
import io.wso2.android.api_authenticator.sdk.providers.authentication.AuthenticationState
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Utility class for the [AuthenticationManager]
 *
 * This class contains utility methods that are used by the [AuthenticationManager]
 */
object AuthenticationManagerUtil {
    /**
     * Emit the success state based on the flow status of the [AuthenticationFlow]
     *
     * @param authenticationFlow [AuthenticationFlow] object
     * @param authStateFlow [MutableStateFlow] of [AuthenticationState]
     */
    fun emitSuccessStateOnFlowStatus(
        authenticationFlow: AuthenticationFlow,
        authStateFlow: MutableStateFlow<AuthenticationState>
    ) {
        when (authenticationFlow.flowStatus) {
            FlowStatus.SUCCESS.flowStatus -> {
                authStateFlow.tryEmit(AuthenticationState.Authorized)
            }

            else -> {
                authStateFlow.tryEmit(
                    AuthenticationState.Unauthorized(
                        authenticationFlow
                    )
                )
            }
        }
    }
}