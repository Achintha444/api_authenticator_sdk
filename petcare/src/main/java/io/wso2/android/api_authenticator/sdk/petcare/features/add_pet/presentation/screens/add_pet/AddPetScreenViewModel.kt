package io.wso2.android.api_authenticator.sdk.petcare.features.add_pet.presentation.screens.add_pet

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.wso2.android.api_authenticator.sdk.petcare.features.home.domain.models.UserDetails
import io.wso2.android.api_authenticator.sdk.petcare.features.home.domain.repository.PetRepository
import io.wso2.android.api_authenticator.sdk.petcare.features.login.domain.repository.AsgardeoAuthRepository
import io.wso2.android.api_authenticator.sdk.petcare.util.navigation.NavigationViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPetScreenViewModel @Inject constructor(
    asgardeoAuthRepository: AsgardeoAuthRepository,
    @ApplicationContext private val applicationContext: Context,
) : ViewModel() {

    companion object {
        const val TAG = "AddPetScreen"
    }

    private val _state = MutableStateFlow(AddPetScreenState())
    val state = _state

    private val tokenProvider = asgardeoAuthRepository.getTokenProvider()

    fun navigateToHome() {
        viewModelScope.launch {
            NavigationViewModel.navigationEvents.emit(
                NavigationViewModel.Companion.NavigationEvent.NavigateToHome
            )
        }
    }
}
