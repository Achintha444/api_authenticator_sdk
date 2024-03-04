package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authorize_flow.AuthorizeFlow
import io.wso2.android.api_authenticator.sdk.models.authorize_flow.AuthorizeFlowNotSuccess
import io.wso2.android.api_authenticator.sdk.models.flow_status.FlowStatus
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
    private val authenticationRepository: AuthenticationRepository
): ViewModel() {

    companion object {
        const val TAG = "AuthScreen"
    }

    private val _state = MutableStateFlow(AuthScreenState())
    val state = _state

    fun setAuthorizeFlow(authorizeFlow: AuthorizeFlow) {
        _state.update {
            it.copy(
                authorizeFlow = authorizeFlow as AuthorizeFlowNotSuccess,
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
                .onRight { authorizeFlow ->
                    // Handle success
                    if(authorizeFlow.flowStatus == FlowStatus.SUCCESS.flowStatus) {
                        sendEvent(Event.Toast("Logged in successfully"))
                        NavigationViewModel.navigationEvents.emit(
                            NavigationViewModel.Companion.NavigationEvent.NavigateToHome
                        )
                    } else {
                        setAuthorizeFlow(authorizeFlow)
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
}
