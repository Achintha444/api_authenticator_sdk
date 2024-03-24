package io.wso2.android.api_authenticator.sdk.provider.providers.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.managers.authenticator.AuthenticatorManager
import io.wso2.android.api_authenticator.sdk.models.auth_params.GoogleNativeAuthenticatorTypeAuthParams
import io.wso2.android.api_authenticator.sdk.models.auth_params.TotpAuthenticatorTypeAuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorTypes
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticatorProviderException
import io.wso2.android.api_authenticator.sdk.models.prompt_type.PromptTypes
import io.wso2.android.api_authenticator.sdk.models.state.AuthenticationState
import io.wso2.android.api_authenticator.sdk.provider.di.AuthenticationProviderContainer
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication.AuthenticationProviderManager
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.token.TokenProviderManager
import io.wso2.android.api_authenticator.sdk.util.AuthenticatorTypeUtil
import kotlinx.coroutines.flow.SharedFlow
import java.lang.ref.WeakReference

/**
 * Authentication provider class that is used to manage the authentication process.
 *
 * @property authenticationCoreConfig [AuthenticationCoreConfig] object
 *
 * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
 * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
 * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
 * emit: [AuthenticationState.Error] - An error occurred during the authentication process
 */
class AuthenticationProvider private constructor(
    private val authenticationCoreConfig: AuthenticationCoreConfig
) {
    /**
     * Instance of the [AuthenticationCoreDef] that will be used throughout the application
     */
    private val authenticationCore: AuthenticationCoreDef by lazy {
        AuthenticationProviderContainer.getAuthenticationCoreDef(authenticationCoreConfig)
    }

    private val authenticationProviderManager: AuthenticationProviderManager by lazy {
        AuthenticationProviderContainer.getAuthenticationProviderManager(authenticationCore)
    }

    companion object {
        /**
         * Instance of the [AuthenticationProvider] that will be used throughout the application
         */
        private var authenticationProviderInstance: WeakReference<AuthenticationProvider> =
            WeakReference(null)

        /**
         * Initialize the [AuthenticationProvider] instance and return the instance.
         *
         * @param authenticationCoreConfig The [AuthenticatorManager] instance
         *
         * @return The [AuthenticationProvider] instance
         */
        fun getInstance(
            authenticationCoreConfig: AuthenticationCoreConfig
        ): AuthenticationProvider {
            var authenticatorProvider = authenticationProviderInstance.get()
            if (authenticatorProvider == null) {
                authenticatorProvider = AuthenticationProvider(authenticationCoreConfig)
                authenticationProviderInstance = WeakReference(authenticatorProvider)
            }
            return authenticatorProvider
        }

        /**
         * Get the [AuthenticationProvider] instance.
         *
         * @return The [AuthenticationProvider] instance
         */
        fun getInstance(): AuthenticationProvider? = authenticationProviderInstance.get()
    }

    /**
     * Get authentication state flow
     *
     * @return authentication state flow [SharedFlow<AuthenticationState>]
     */
    fun getAuthenticationStateFlow(): SharedFlow<AuthenticationState> =
        authenticationProviderManager.getAuthenticationStateFlow()

    /**
     * Check whether the user is logged in or not.
     *
     * @return `true` if the user is logged in, `false` otherwise
     */
    suspend fun isLoggedIn(context: Context): Boolean =
        authenticationProviderManager.isLoggedIn(context)

    /**
     * Handle the authentication flow initially to check whether the user is authenticated or not.
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Initial] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    suspend fun isLoggedInStateFlow(context: Context) =
        authenticationProviderManager.isLoggedInStateFlow(context)

    /**
     * Initialize the authentication process.
     * This method will initialize the authentication process and emit the state of the authentication process.
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    suspend fun initializeAuthentication(context: Context) =
        authenticationProviderManager.initializeAuthentication(context)

    /**
     * Authenticate the user with the username and password.
     *
     * @param context The context of the application
     * @param username The username of the user
     * @param password The password of the user
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    suspend fun authenticateWithUsernameAndPassword(
        context: Context,
        username: String,
        password: String
    ) = authenticationProviderManager
        .authenticateWithUsernameAndPassword(context, username, password)

    /**
     * Authenticate the user with the TOTP token.
     *
     * @param context The context of the application
     * @param token The TOTP token of the user
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    suspend fun authenticateWithTotp(context: Context, token: String) =
        authenticationProviderManager.authenticateWithTotp(context, token)

    /**
     * Authenticate the user with the selected authenticator which requires a redirect URI.
     *
     * @param context The context of the application
     * @param authenticatorId The authenticator id of the selected authenticator
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     */
    suspend fun authenticateWithRedirectUri(
        context: Context,
        authenticatorId: String? = null,
        authenticatorType: String? = null
    ) = authenticationProviderManager
        .authenticateWithRedirectUri(context, authenticatorId, authenticatorType)

    /**
     * Handle the redirect URI and authenticate the user with the selected authenticator.
     *
     * @param context The context of the application
     * @param deepLink The deep link URI that is received from the redirect URI
     *
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    internal suspend fun handleRedirectUri(context: Context, deepLink: Uri) =
        authenticationProviderManager.handleRedirectUri(context, deepLink)

    /**
     * Authenticate the user with the OpenID Connect authenticator.
     *
     * @param context The context of the application
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    suspend fun authenticateWithOpenIdConnect(context: Context) =
        authenticationProviderManager.authenticateWithOpenIdConnect(context)

    /**
     * Authenticate the user with the Google authenticator.
     *
     * @param context The context of the application
     * @param googleAuthenticateResultLauncher The [ActivityResultLauncher] object to handle the Google authentication result
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    suspend fun authenticateWithGoogle(
        context: Context,
        googleAuthenticateResultLauncher: ActivityResultLauncher<Intent>
    ) = authenticationProviderManager
        .authenticateWithGoogle(
            context,
            authenticationCoreConfig.getGoogleWebClientId()!!,
            googleAuthenticateResultLauncher
        )

    /**
     * Handle the Google authentication result.
     *
     * @param context The context of the application
     * @param result The [ActivityResult] object that contains the result of the Google authentication process
     *
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     */
    suspend fun handleGoogleAuthenticateResult(
        context: Context,
        result: ActivityResult
    ) = authenticationProviderManager.handleGoogleAuthenticateResult(context, result)

    /**
     * Authenticate the user with the selected authenticator.
     *
     * @param context The context of the application
     * @param authenticatorId The authenticator id of the selected authenticator
     * @param authParams The authentication parameters of the selected authenticator
     * as a LinkedHashMap<String, String> with the key as the parameter name and the value as the
     * parameter value
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    suspend fun authenticateWithAnyAuthenticator(
        context: Context,
        authenticatorId: String,
        authParams: LinkedHashMap<String, String>
    ) = authenticationProviderManager
        .authenticateWithAnyAuthenticator(context, authenticatorId, authParams)

    /**
     * Logout the user from the application.
     *
     * @param context The context of the application
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Initial] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    suspend fun logout(context: Context) = authenticationProviderManager.logout(
        context,
        authenticationCoreConfig.getClientId()
    )
}