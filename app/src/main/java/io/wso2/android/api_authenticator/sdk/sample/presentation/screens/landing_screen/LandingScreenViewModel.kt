package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.landing_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.wso2.android.api_authenticator.sdk.models.authorize_flow.AuthorizeFlow
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.AuthenticationRepository
import io.wso2.android.api_authenticator.sdk.sample.presentation.util.sendEvent
import io.wso2.android.api_authenticator.sdk.sample.util.Event
import io.wso2.android.api_authenticator.sdk.sample.util.JsonUtil
import io.wso2.android.api_authenticator.sdk.sample.util.navigation.NavigationViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLEncoder
import javax.inject.Inject

@HiltViewModel
class LandingScreenViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {
    companion object {
        const val TAG = "LandingScreen"
    }

    private val _state = MutableStateFlow(LandingScreenState())
    val state = _state

    fun authorize() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            authenticationRepository.authorize()
                .onRight { authorizeFlow ->
                    NavigationViewModel.navigationEvents.emit(
                        NavigationViewModel.Companion.NavigationEvent.NavigateToAuthWithData(
                            URLEncoder.encode(authorizeFlow.toJsonString(), "utf-8")
                        )
                    )
                }
                .onLeft { authenticationError ->
                    // Handle error
                    _state.update {
                        it.copy(error = authenticationError.toString())
                    }
                    sendEvent(Event.Toast(authenticationError.toString()))
                }
            _state.update {
                it.copy(isLoading = false)
            }
        }
    }
}
