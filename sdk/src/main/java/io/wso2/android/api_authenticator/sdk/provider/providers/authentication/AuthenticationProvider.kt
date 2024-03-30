package io.wso2.android.api_authenticator.sdk.provider.providers.authentication

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import io.wso2.android.api_authenticator.sdk.models.state.AuthenticationState
import kotlinx.coroutines.flow.SharedFlow

/**
 * Interface for the authentication provider to be implemented by the application.
 * This has the methods to handle the authentication process.
 */
interface AuthenticationProvider {
    /**
     * Get authentication state flow
     *
     * @return authentication state flow [SharedFlow<AuthenticationState>]
     */
    fun getAuthenticationStateFlow(): SharedFlow<AuthenticationState>

    /**
     * Check whether the user is logged in or not.
     *
     * @return `true` if the user is logged in, `false` otherwise
     */
    suspend fun isLoggedIn(context: Context): Boolean

    /**
     * Handle the authentication flow initially to check whether the user is authenticated or not.
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Initial] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    suspend fun isLoggedInStateFlow(context: Context)

    /**
     * Initialize the authentication process.
     * This method will initialize the authentication process and emit the state of the authentication process.
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    suspend fun initializeAuthentication(context: Context)

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
    )

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
    suspend fun authenticateWithTotp(context: Context, token: String)

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
    )

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
    suspend fun authenticateWithOpenIdConnect(context: Context)

    /**
     * Authenticate the user with the Google authenticator.
     *
     * @param context The context of the application
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun authenticateWithGoogle(context: Context)

    /**
     * Authenticate the user with the Google authenticator using the legacy one tap method.
     *
     * @param context The context of the application
     * @param googleAuthenticateResultLauncher The result launcher for the Google authentication process
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    suspend fun authenticateWithGoogleLegacy(
        context: Context,
        googleAuthenticateResultLauncher: ActivityResultLauncher<Intent>
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
    suspend fun handleGoogleNativeLegacyAuthenticateResult(
        context: Context,
        result: ActivityResult
    )

    /**
     * Authenticate the user with the Github authenticator (Redirect).
     *
     * @param context The context of the application
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
     suspend fun authenticateWithGithubRedirect(context: Context)

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
    )

    /**
     * Logout the user from the application.
     *
     * @param context The context of the application
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Initial] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    suspend fun logout(context: Context)
}
