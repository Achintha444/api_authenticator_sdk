package io.wso2.android.api_authenticator.sdk.sample.presentation.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.wso2.android.api_authenticator.sdk.sample.util.Event
import io.wso2.android.api_authenticator.sdk.sample.util.EventBus
import kotlinx.coroutines.launch

fun ViewModel.sendEvent(event: Event) {
    viewModelScope.launch {
        EventBus.sendEvent(event)
    }
}