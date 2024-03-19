package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.home_screen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.ProviderRepository
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    providerRepository: ProviderRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    companion object {
        const val TAG = "HomeScreen"
    }

    private val _state = MutableStateFlow(HomeScreenState())
    val state = _state

    private val tokenManager = providerRepository.getTokenProvider()

    init {
        getUserDetails()
    }

    private fun getUserDetails() {
        _state.update {
            it.copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            try {
                tokenManager.performActionWithFreshTokens(applicationContext) { accessToken, _ ->
                    val user = userRepository.getUserDetails(accessToken!!)
                    _state.update {
                        it.copy(user = user)
                    }
                }

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message!!
                    )
                }
            } finally {
                _state.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }
        }
    }
}
