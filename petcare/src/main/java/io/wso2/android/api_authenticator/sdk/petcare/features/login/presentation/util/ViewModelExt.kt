package io.wso2.android.api_authenticator.sdk.petcare.features.login.presentation.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.wso2.android.api_authenticator.sdk.petcare.util.Event
import io.wso2.android.api_authenticator.sdk.petcare.util.EventBus
import kotlinx.coroutines.launch

fun ViewModel.sendEvent(event: Event) {
    viewModelScope.launch {
        EventBus.sendEvent(event)
    }
}