package io.wso2.android.api_authenticator.sdk.petcare.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

object UiUtil {
    @Composable
    fun height(): Int {
        val configuration = LocalConfiguration.current
        return configuration.screenHeightDp
    }
    @Composable
    fun width(): Int {
        val configuration = LocalConfiguration.current
        return configuration.screenWidthDp
    }
}