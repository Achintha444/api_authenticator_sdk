package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.landing_screen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.wso2.android.api_authenticator.sdk.models.state.AuthenticationState
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.AuthenticationRepository
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.ProviderRepository
import io.wso2.android.api_authenticator.sdk.sample.presentation.util.sendEvent
import io.wso2.android.api_authenticator.sdk.sample.util.Event
import io.wso2.android.api_authenticator.sdk.sample.util.navigation.NavigationViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLEncoder
import javax.inject.Inject

@HiltViewModel
class LandingScreenViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val authenticationRepository: AuthenticationRepository,
    private val providerRepository: ProviderRepository
) : ViewModel() {
    companion object {
        const val TAG = "LandingScreen"
    }

    private val _state = MutableStateFlow(LandingScreenState())
    val state = _state

    private val authenticationProvider = providerRepository.getAuthenticationProvider()
    private val authenticationStateFlow = authenticationProvider.getAuthenticationStateFlow()

    init {
        handleAuthenticationState()
        isLoggedInStateFlow()
    }

    fun authorize() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            authenticationRepository.authorize()
                .onRight { authenticationFlow ->
                    NavigationViewModel.navigationEvents.emit(
                        NavigationViewModel.Companion.NavigationEvent.NavigateToAuthWithData(
                            URLEncoder.encode(authenticationFlow.toJsonString(), "utf-8")
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

    fun initializeAuthentication() {
        viewModelScope.launch {
            authenticationProvider.initializeAuthentication(applicationContext)
        }
    }

    private fun isLoggedInStateFlow() {
        viewModelScope.launch {
            authenticationProvider.isLoggedInStateFlow(applicationContext)
        }
    }

    private fun handleAuthenticationState() {
        viewModelScope.launch {
            authenticationStateFlow.collect {
                when (it) {
                    is AuthenticationState.Initial -> {
                        _state.update { landingScreenState ->
                            landingScreenState.copy(isLoading = false)
                        }
                    }

                    is AuthenticationState.Unauthenticated -> {
                        _state.update { landingScreenState ->
                            landingScreenState.copy(isLoading = false)
                        }
                        NavigationViewModel.navigationEvents.emit(
                            NavigationViewModel.Companion.NavigationEvent.NavigateToAuthWithData(
                                URLEncoder.encode(it.authenticationFlow!!.toJsonString(), "utf-8")
                            )
                        )
                    }

                    is AuthenticationState.Error -> {
                        _state.update { landingScreenState ->
                            landingScreenState.copy(error = it.toString(), isLoading = false)
                        }
                        sendEvent(Event.Toast(it.toString()))
                    }

                    is AuthenticationState.Authenticated -> {
                        _state.update { landingScreenState ->
                            landingScreenState.copy(isLoading = false)
                        }
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
}
