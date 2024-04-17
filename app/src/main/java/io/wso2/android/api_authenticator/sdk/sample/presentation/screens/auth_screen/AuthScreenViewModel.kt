package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.AuthenticationRepository
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.GoogleAuthenticationRepository
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.ProviderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthScreenViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val authenticationRepository: AuthenticationRepository,
    private val googleAuthenticationRepository: GoogleAuthenticationRepository,
    providerRepository: ProviderRepository
) : ViewModel() {

    companion object {
        const val TAG = "AuthScreen"
    }

    private val _state = MutableStateFlow(AuthScreenState())
    val state = _state

    private val authenticationProvider = providerRepository.getAuthenticationProvider()

    fun setAuthenticationFlow(authenticationFlow: AuthenticationFlow) {
        _state.update {
            it.copy(
                authenticationFlow = authenticationFlow as AuthenticationFlowNotSuccess,
                isLoading = false
            )
        }
    }

    fun authenticateWithUsernamePassword(
        authenticatorId: String,
        username: String,
        password: String
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            authenticationProvider.authenticateWithUsernameAndPassword(
                applicationContext,
                authenticatorId,
                username,
                password
            )
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun authenticateWithTotp(authenticatorId: String, token: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            authenticationProvider.authenticateWithTotp(
                applicationContext,
                authenticatorId,
                token
            )
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun authenticateWithOpenIdConnect(authenticatorId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            authenticationProvider.authenticateWithOpenIdConnect(
                applicationContext,
                authenticatorId
            )
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun authenticateWithGoogleNative(authenticatorId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            authenticationProvider.authenticateWithGoogleNative(applicationContext, authenticatorId)
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun authenticateWithPasskey(authenticatorId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            authenticationProvider.authenticateWithPasskey(applicationContext, authenticatorId)
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun authenticateWithGoogleNativeLegacy(
        authenticatorId: String,
        googleAuthenticateResultLauncher: ActivityResultLauncher<Intent>
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            authenticationProvider.authenticateWithGoogleNativeLegacy(
                applicationContext,
                authenticatorId,
                googleAuthenticateResultLauncher
            )
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }

    fun handleGoogleNativeLegacyAuthenticateResult(result: ActivityResult) {
        viewModelScope.launch {
            authenticationProvider.handleGoogleNativeLegacyAuthenticateResult(
                applicationContext,
                result.resultCode,
                result.data!!
            )
        }
    }

    fun authenticateWithGithub(authenticatorId: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            authenticationProvider.authenticateWithGithub(
                applicationContext,
                authenticatorId
            )
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        }
    }
}
