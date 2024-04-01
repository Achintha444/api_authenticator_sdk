package io.wso2.android.api_authenticator.sdk.core.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import io.wso2.android.api_authenticator.sdk.core.di.RedirectUriReceiverActivityContainer
import io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.redirect.RedirectAuthenticationHandlerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Activity to receive the deep link redirection URI
 */
class RedirectUriReceiverActivity : ComponentActivity() {
    // The authentication provider
    private val redirectAuthenticationManager: RedirectAuthenticationHandlerManager? by lazy {
        RedirectUriReceiverActivityContainer.getRedirectAuthenticationHandlerManager()
    }

    // Flag to check if the redirection is handled
    private var redirectionHandled: Boolean = false

    companion object {
        private const val KEY_REDIRECTION_HANDLED = "redirectionHandled"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle the redirection URI
        if (savedInstanceState == null || !savedInstanceState.getBoolean(KEY_REDIRECTION_HANDLED)) {
            intent?.data?.let { deepLink ->
                redirectionHandled = true // Mark redirection as handled

                // Handle the redirection URI in a coroutine
                CoroutineScope(Dispatchers.IO).launch {
                    redirectAuthenticationManager!!.handleRedirectUri(
                        this@RedirectUriReceiverActivity,
                        deepLink
                    )
                }
            }
        } else {
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_REDIRECTION_HANDLED, redirectionHandled)
    }
}
