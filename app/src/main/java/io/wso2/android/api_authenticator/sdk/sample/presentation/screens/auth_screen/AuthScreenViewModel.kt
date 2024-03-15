package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess
import io.wso2.android.api_authenticator.sdk.models.flow_status.FlowStatus
import io.wso2.android.api_authenticator.sdk.providers.authentication.AuthenticationState
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.AuthenticationProviderRepository
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.AuthenticationRepository
import io.wso2.android.api_authenticator.sdk.sample.presentation.util.sendEvent
import io.wso2.android.api_authenticator.sdk.sample.util.Event
import io.wso2.android.api_authenticator.sdk.sample.util.navigation.NavigationViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLEncoder
import javax.inject.Inject

@HiltViewModel
class AuthScreenViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val authenticationProviderRepository: AuthenticationProviderRepository
): ViewModel() {

    companion object {
        const val TAG = "AuthScreen"
    }

    private val _state = MutableStateFlow(AuthScreenState())
    val state = _state

    private val authenticationManager = authenticationProviderRepository.getAuthenticationManager()
    private val authenticationStateFlow = authenticationManager.authenticationStateFlow

    init {
        viewModelScope.launch {
            handleAuthenticationState()
        }
    }

    fun setAuthenticationFlow(authenticationFlow: AuthenticationFlow) {
        _state.update {
            it.copy(
                authenticationFlow = authenticationFlow as AuthenticationFlowNotSuccess,
                isLoading = false
            )
        }
    }

    fun authenticate(
        authenticatorType: AuthenticatorType,
        authenticatorParameters: AuthParams
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            authenticationRepository.authenticate(
                authenticatorType,
                authenticatorParameters
            )
                .onRight { authenticationFlow ->
                    // TODO: Move this to MutableSharedFlow
                    // Handle success
                    if(authenticationFlow.flowStatus == FlowStatus.SUCCESS.flowStatus) {
                        sendEvent(Event.Toast("Logged in successfully"))
                        NavigationViewModel.navigationEvents.emit(
                            NavigationViewModel.Companion.NavigationEvent.NavigateToHome
                        )
                    } else {
                        setAuthenticationFlow(authenticationFlow)
                    }
                }
                .onLeft { authenticationError ->
                    // Handle error
                    _state.update {
                        it.copy(error = authenticationError.errorMessage)
                    }
                    sendEvent(Event.Toast(authenticationError.errorMessage))
                }
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun authenticateWithUsernamePassword(
        username: String,
        password: String
    ) {
        viewModelScope.launch {
            authenticationManager.authenticateWithUsernameAndPassword(
                username,
                password
            )
        }
    }

    fun authenticateWithTotp(
        token: String
    ) {
        viewModelScope.launch {
            authenticationManager.authenticateWithTotp(
                token
            )
        }
    }

    private suspend fun handleAuthenticationState() {
        authenticationStateFlow.collect {
            when (it) {
                is AuthenticationState.Unauthorized -> {
                    setAuthenticationFlow(it.authenticationFlow!!)
                }

                is AuthenticationState.Error -> {
                    _state.update { landingScreenState ->
                        landingScreenState.copy(error = it.toString())
                    }
                    sendEvent(Event.Toast(it.toString()))
                }

                is AuthenticationState.Authorized -> {
                    NavigationViewModel.navigationEvents.emit(
                        NavigationViewModel.Companion.NavigationEvent.NavigateToHome
                    )
                }

                else -> {
                    _state.update { landingScreenState ->
                        landingScreenState.copy(isLoading = true)
                    }
                }
            }
        }
    }
}
