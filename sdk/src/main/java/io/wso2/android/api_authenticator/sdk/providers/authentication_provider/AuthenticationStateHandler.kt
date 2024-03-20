package io.wso2.android.api_authenticator.sdk.providers.authentication_provider

import android.content.Context
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowSuccess
import io.wso2.android.api_authenticator.sdk.models.flow_status.FlowStatus
import io.wso2.android.api_authenticator.sdk.models.state.AuthenticationState
import io.wso2.android.api_authenticator.sdk.models.state.TokenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * [AuthenticationStateHandler] is responsible for handling the authentication state changes.
 */
object AuthenticationStateHandler {
    // The authentication state flow
    private val _authenticationStateFlow = MutableStateFlow<AuthenticationState>(
        AuthenticationState.Initial
    )
    // The authentication state flow as a state flow
    val authenticationStateFlow: StateFlow<AuthenticationState> = _authenticationStateFlow

    /**
     * Emit the authentication state.
     */
    fun emitAuthenticationState(state: AuthenticationState) {
        _authenticationStateFlow.tryEmit(state)
    }

    /**
     * Handle the authentication flow result.
     */
    suspend fun handleAuthenticationFlowResult(
        authenticationFlow: AuthenticationFlow,
        context: Context,
        exchangeAuthorizationCode: suspend (code: String, context: Context) -> TokenState?,
        saveTokenState: suspend (context: Context, tokenState: TokenState) -> Unit
    ): ArrayList<AuthenticatorType>? {
        when (authenticationFlow.flowStatus) {
            FlowStatus.SUCCESS.flowStatus -> {
                // Exchange the authorization code for the access token and save the tokens
                runCatching {
                    val tokenState: TokenState? = exchangeAuthorizationCode(
                        (authenticationFlow as AuthenticationFlowSuccess).authData.code,
                        context
                    )
                    saveTokenState(context, tokenState!!)
                }.onSuccess {
                    emitAuthenticationState(AuthenticationState.Authenticated)
                }.onFailure {
                    emitAuthenticationState(AuthenticationState.Error(it))
                }

                return null
            }
            else -> {
                emitAuthenticationState(AuthenticationState.Unauthenticated(authenticationFlow))

                return (authenticationFlow as AuthenticationFlowNotSuccess).nextStep.authenticators
            }
        }
    }
}