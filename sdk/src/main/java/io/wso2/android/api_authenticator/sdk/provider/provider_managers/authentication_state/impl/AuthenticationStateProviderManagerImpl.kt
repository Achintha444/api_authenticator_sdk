package io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication_state.impl

import android.content.Context
import io.wso2.android.api_authenticator.sdk.core.core_types.authentication.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowSuccess
import io.wso2.android.api_authenticator.sdk.models.flow_status.FlowStatus
import io.wso2.android.api_authenticator.sdk.models.state.AuthenticationState
import io.wso2.android.api_authenticator.sdk.models.state.TokenState
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication_state.AuthenticationStateProviderManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import java.lang.ref.WeakReference

/**
 * Authentication state provider manager that is used to manage the authentication state.
 *
 * @property authenticationCore The [AuthenticationCoreDef] instance
 *
 * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
 * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
 * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
 * emit: [AuthenticationState.Error] - An error occurred during the authentication process
 */
internal class AuthenticationStateProviderManagerImpl private constructor(
    private val authenticationCore: AuthenticationCoreDef,
) : AuthenticationStateProviderManager {

    companion object {
        /**
         * Instance of the [AuthenticationStateProviderManagerImpl] to use in the
         * application
         */
        private var authenticationStateProviderManagerInstance:
                WeakReference<AuthenticationStateProviderManagerImpl> =
            WeakReference(null)

        /**
         * Initialize the [AuthenticationStateProviderManagerImpl] instance and return the instance.
         *
         * @param authenticationCore The [AuthenticationCoreDef] instance
         *
         * @return The [AuthenticationStateProviderManagerImpl] instance
         */
        fun getInstance(
            authenticationCore: AuthenticationCoreDef
        ): AuthenticationStateProviderManagerImpl {
            var authenticatorStateProviderManager = authenticationStateProviderManagerInstance.get()
            if (authenticatorStateProviderManager == null) {
                authenticatorStateProviderManager = AuthenticationStateProviderManagerImpl(
                    authenticationCore
                )
                authenticationStateProviderManagerInstance =
                    WeakReference(authenticatorStateProviderManager)
            }
            return authenticatorStateProviderManager
        }
    }

    // The authentication state flow
    private val _authenticationStateFlow = MutableStateFlow<AuthenticationState>(
        AuthenticationState.Initial
    )

    // The authentication state flow as a state flow
    private val authenticationStateFlow: StateFlow<AuthenticationState> = _authenticationStateFlow

    /**
     * Get authentication state flow
     *
     * @return authentication state flow [SharedFlow<AuthenticationState>]
     */
    override fun getAuthenticationStateFlow(): SharedFlow<AuthenticationState> =
        authenticationStateFlow

    /**
     * Emit the authentication state.
     *
     * @param state The [AuthenticationState] to emit
     */
    override fun emitAuthenticationState(state: AuthenticationState) {
        _authenticationStateFlow.tryEmit(state)
    }

    /**
     * Handle the authentication flow result.
     *
     * @param authenticationFlow The [AuthenticationFlow] to handle
     * @param context The context of the application
     *
     * @return The list of [AuthenticatorType] to use next
     */
    override suspend fun handleAuthenticationFlowResult(
        authenticationFlow: AuthenticationFlow,
        context: Context
    ): ArrayList<AuthenticatorType>? {
        when (authenticationFlow.flowStatus) {
            FlowStatus.SUCCESS.flowStatus -> {
                // Exchange the authorization code for the access token and save the tokens
                runCatching {
                    val tokenState: TokenState? = authenticationCore.exchangeAuthorizationCode(
                        (authenticationFlow as AuthenticationFlowSuccess).authData.code,
                        context
                    )
                    authenticationCore.saveTokenState(context, tokenState!!)
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