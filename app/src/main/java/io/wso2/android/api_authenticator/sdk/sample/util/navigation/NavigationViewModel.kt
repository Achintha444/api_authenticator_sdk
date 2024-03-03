package io.wso2.android.api_authenticator.sdk.sample.util.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {
    companion object {
        val navigationEvents = MutableSharedFlow<NavigationEvent>()

        sealed class NavigationEvent {
            object NavigateBack : NavigationEvent()
            object NavigateToHome : NavigationEvent()
            data class NavigateToAuthWithData(val data: String) : NavigationEvent()
        }
    }

}