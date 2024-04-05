package io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication.impl

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.wso2.android.api_authenticator.sdk.core.core_types.authentication.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.models.auth_params.BasicAuthenticatorAuthParams
import io.wso2.android.api_authenticator.sdk.models.auth_params.TotpAuthenticatorTypeAuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorTypes
import io.wso2.android.api_authenticator.sdk.models.state.AuthenticationState
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authenticate_handler.AuthenticateHandlerProviderManager
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication.AuthenticationProviderManager
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication_state.AuthenticationStateProviderManager
import kotlinx.coroutines.flow.SharedFlow
import java.lang.ref.WeakReference

/**
 * Authentication provider manager that is used to manage the authentication process.
 *
 * @property authenticationCore The [AuthenticationCoreDef] instance
 * @property authenticationStateProviderManager The [AuthenticationStateProviderManager] instance
 *
 * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
 * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
 * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
 * emit: [AuthenticationState.Error] - An error occurred during the authentication process
 */
internal class AuthenticationProviderManagerImpl private constructor(
    private val authenticationCore: AuthenticationCoreDef,
    private val authenticationStateProviderManager: AuthenticationStateProviderManager,
    private val authenticateHandlerProviderManager: AuthenticateHandlerProviderManager
) : AuthenticationProviderManager {
    companion object {
        /**
         * Instance of the [AuthenticationProviderManagerImpl] that will be used throughout the
         * application
         */
        private var authenticationProviderManagerInstance:
                WeakReference<AuthenticationProviderManagerImpl> =
            WeakReference(null)

        /**
         * Initialize the [AuthenticationProviderManagerImpl] instance and return the instance.
         *
         * @param authenticationCore The [AuthenticationCoreDef] instance
         * @param authenticationStateProviderManager The [AuthenticationStateProviderManager] instance
         *
         * @return The [AuthenticationProviderManagerImpl] instance
         */
        fun getInstance(
            authenticationCore: AuthenticationCoreDef,
            authenticationStateProviderManager: AuthenticationStateProviderManager,
            authenticateHandlerProviderManager: AuthenticateHandlerProviderManager
        ): AuthenticationProviderManagerImpl {
            var authenticationProviderManager = authenticationProviderManagerInstance.get()
            if (authenticationProviderManager == null) {
                authenticationProviderManager = AuthenticationProviderManagerImpl(
                    authenticationCore,
                    authenticationStateProviderManager,
                    authenticateHandlerProviderManager
                )
                authenticationProviderManagerInstance = WeakReference(authenticationProviderManager)
            }
            return authenticationProviderManager
        }

        /**
         * Get the [AuthenticationProviderManagerImpl] instance.
         *
         * @return The [AuthenticationProviderManagerImpl] instance
         */
        fun getInstance(): AuthenticationProviderManagerImpl? =
            authenticationProviderManagerInstance.get()
    }

    /**
     * Get authentication state flow of the authentication state which is exposed to the outside.
     *
     * @return authentication state flow [SharedFlow<AuthenticationState>]
     */
    override fun getAuthenticationStateFlow(): SharedFlow<AuthenticationState> =
        authenticationStateProviderManager.getAuthenticationStateFlow()

    /**
     * Check whether the user is logged in or not.
     *
     * @return `true` if the user is logged in, `false` otherwise
     */
    override suspend fun isLoggedIn(context: Context): Boolean =
        authenticationCore.validateAccessToken(context) ?: false

    /**
     * Handle the authentication flow initially to check whether the user is authenticated or not.
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Initial] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    override suspend fun isLoggedInStateFlow(context: Context) {
        authenticationStateProviderManager.emitAuthenticationState(AuthenticationState.Loading)

        // TODO: Remove this block
        authenticationCore.clearTokens(context)

        runCatching {
            authenticationCore.validateAccessToken(context)
        }.onSuccess { isAccessTokenValid ->
            if (isAccessTokenValid == true) {
                authenticationStateProviderManager.emitAuthenticationState(
                    AuthenticationState.Authenticated
                )
            } else {
                authenticationStateProviderManager.emitAuthenticationState(AuthenticationState.Initial)
            }
        }.onFailure {
            authenticationStateProviderManager.emitAuthenticationState(AuthenticationState.Initial)
        }
    }

    /**
     * Initialize the authentication process.
     * This method will initialize the authentication process and emit the state of the authentication process.
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    override suspend fun initializeAuthentication(context: Context) {
        authenticationStateProviderManager.emitAuthenticationState(AuthenticationState.Loading)

        runCatching {
            authenticationCore.authorize()
        }.onSuccess {
            authenticateHandlerProviderManager.setAuthenticatorsInThisStep(
                authenticationStateProviderManager.handleAuthenticationFlowResult(
                    it!!,
                    context
                )
            )
        }.onFailure {
            authenticationStateProviderManager.emitAuthenticationState(AuthenticationState.Error(it))
        }
    }

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
    override suspend fun authenticateWithUsernameAndPassword(
        context: Context,
        username: String,
        password: String
    ) {
        authenticateHandlerProviderManager.authenticateWithAuthenticator(
            authenticatorTypeString = AuthenticatorTypes.BASIC_AUTHENTICATOR.authenticatorType
        ) {
            authenticateHandlerProviderManager.commonAuthenticate(
                context,
                userSelectedAuthenticatorType = it,
                authParams = BasicAuthenticatorAuthParams(username, password)
            )
        }
    }

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
    override suspend fun authenticateWithTotp(context: Context, token: String) {
        authenticateHandlerProviderManager.authenticateWithAuthenticator(
            authenticatorTypeString = AuthenticatorTypes.TOTP_AUTHENTICATOR.authenticatorType
        ) {
            authenticateHandlerProviderManager.commonAuthenticate(
                context,
                userSelectedAuthenticatorType = it,
                authParams = TotpAuthenticatorTypeAuthParams(token)
            )
        }
    }

    /**
     * Authenticate the user with the selected authenticator which requires a redirect URI.
     *
     * @param context The context of the application
     * @param authenticatorIdString The authenticator id of the selected authenticator
     * @param authenticatorTypeString The authenticator type of the selected authenticator
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     */
    override suspend fun authenticateWithRedirectUri(
        context: Context,
        authenticatorIdString: String?,
        authenticatorTypeString: String?
    ) {
        authenticateHandlerProviderManager.authenticateWithAuthenticator(
            authenticatorTypeString = authenticatorTypeString,
            authenticatorIdString = authenticatorIdString
        ) {
            authenticateHandlerProviderManager.redirectAuthenticate(context, it)
        }
    }

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
    override suspend fun authenticateWithOpenIdConnect(context: Context) {
        authenticateWithRedirectUri(
            context,
            authenticatorTypeString = AuthenticatorTypes.OPENID_CONNECT_AUTHENTICATOR.authenticatorType
        )
    }

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
    override suspend fun authenticateWithGithubRedirect(context: Context) {
        authenticateWithRedirectUri(
            context,
            authenticatorTypeString = AuthenticatorTypes.GITHUB_REDIRECT_AUTHENTICATOR.authenticatorType
        )
    }

    /**
     * Authenticate the user with the Microsoft authenticator (Redirect).
     *
     * @param context The context of the application
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    override suspend fun authenticateWithMicrosoftRedirect(context: Context) {
        authenticateWithRedirectUri(
            context,
            authenticatorTypeString = AuthenticatorTypes.MICROSOFT_REDIRECT_AUTHENTICATOR.authenticatorType
        )
    }

    /**
     * Authenticate the user with the Google authenticator using the Credential Manager API.
     *
     * @param context The context of the application
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun authenticateWithGoogle(context: Context) {
        authenticateHandlerProviderManager.authenticateWithAuthenticator(
            authenticatorTypeString = AuthenticatorTypes.GOOGLE_AUTHENTICATOR.authenticatorType
        ) {
            authenticateHandlerProviderManager.googleAuthenticate(context)
        }
    }

    /**
     * Authenticate the user with the Google authenticator using the legacy one tap method.
     *
     * @param context The context of the application
     * @param googleAuthenticateResultLauncher The result launcher for the Google authentication process
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    override suspend fun authenticateWithGoogleLegacy(
        context: Context,
        googleAuthenticateResultLauncher: ActivityResultLauncher<Intent>
    ) {
        authenticateHandlerProviderManager.authenticateWithAuthenticator(
            authenticatorTypeString = AuthenticatorTypes.GOOGLE_AUTHENTICATOR.authenticatorType
        ) {
            authenticateHandlerProviderManager.googleLegacyAuthenticate(
                context,
                googleAuthenticateResultLauncher
            )
        }
    }

    /**
     * Handle the Google authentication result.
     *
     * @param context The context of the application
     * @param resultCode The result code of the Google authentication process
     * @param data The [Intent] object that contains the result of the Google authentication process
     *
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     */
    override suspend fun handleGoogleNativeLegacyAuthenticateResult(
        context: Context,
        resultCode: Int,
        data: Intent
    ) {
        authenticateHandlerProviderManager.handleGoogleNativeLegacyAuthenticateResult(
            context,
            resultCode,
            data
        )
    }

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
    override suspend fun authenticateWithAnyAuthenticator(
        context: Context,
        authenticatorId: String,
        authParams: LinkedHashMap<String, String>
    ) {
        authenticateHandlerProviderManager.authenticateWithAuthenticator(
            authenticatorIdString = authenticatorId
        ) {
            authenticateHandlerProviderManager.commonAuthenticate(
                context,
                userSelectedAuthenticatorType = it,
                authParamsAsMap = authParams
            )
        }
    }

    /**
     * Authenticate the user with the Passkey authenticator.
     *
     * @param context The context of the application
     * @param allowCredentials The list of allowed credentials. Default is empty array.
     * @param timeout Timeout for the authentication. Default is 300000.
     * @param userVerification User verification method. Default is "required"
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun authenticateWithPasskey(
        context: Context,
        allowCredentials: List<String>?,
        timeout: Long?,
        userVerification: String?
    ) {
        authenticateHandlerProviderManager.authenticateWithAuthenticator(
            authenticatorTypeString = AuthenticatorTypes.PASSKEY_AUTHENTICATOR.authenticatorType
        ) {
            authenticateHandlerProviderManager.passkeyAuthenticate(
                context,
                it,
                allowCredentials,
                timeout,
                userVerification
            )
        }
    }

    /**
     * Logout the user from the application.
     *
     * @param context The context of the application
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Initial] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    override suspend fun logout(context: Context) {
        authenticationStateProviderManager.emitAuthenticationState(AuthenticationState.Loading)

        runCatching {
            val idToken: String? = authenticationCore.getIDToken(context)
            // Call the logout endpoint
            authenticationCore.logout(idToken!!)

            // Sign out from google if the user is signed in from google
            GoogleSignIn.getClient(
                context,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            ).signOut()

            // clear the tokens
            authenticationCore.clearTokens(context)
        }.onSuccess {
            authenticationStateProviderManager.emitAuthenticationState(AuthenticationState.Initial)
        }.onFailure {
            authenticationStateProviderManager.emitAuthenticationState(
                AuthenticationState.Error(it)
            )
        }
    }
}
