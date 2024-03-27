package io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication.impl

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
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.managers.authenticator.AuthenticatorManager
import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.auth_params.BasicAuthenticatorAuthParams
import io.wso2.android.api_authenticator.sdk.models.auth_params.GoogleNativeAuthenticatorTypeAuthParams
import io.wso2.android.api_authenticator.sdk.models.auth_params.TotpAuthenticatorTypeAuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorTypes
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticatorProviderException
import io.wso2.android.api_authenticator.sdk.models.prompt_type.PromptTypes
import io.wso2.android.api_authenticator.sdk.models.state.AuthenticationState
import io.wso2.android.api_authenticator.sdk.provider.di.AuthenticationProviderManagerImplContainer
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication.AuthenticationProviderManager
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication_state.AuthenticationStateProviderManager
import io.wso2.android.api_authenticator.sdk.util.AuthenticatorTypeUtil
import kotlinx.coroutines.CompletableDeferred
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
    private val authenticationStateProviderManager: AuthenticationStateProviderManager
) : AuthenticationProviderManager {
    /**
     * List of authenticators in this step of the authentication flow.
     */
    private var authenticatorsInThisStep: ArrayList<AuthenticatorType>? = null

    /**
     * The selected authenticator for the authentication process.
     */
    private var selectedAuthenticator: AuthenticatorType? = null

    /**
     * Deferred object to wait for the result of the redirect authentication process.
     */
    private val redirectAuthenticationResultDeferred: CompletableDeferred<Unit> by lazy {
        CompletableDeferred()
    }

    /**
     * Deferred object to wait for the result of the Google authentication process.
     */
    private val googleAuthenticationResultDeferred: CompletableDeferred<Unit> by lazy {
        CompletableDeferred()
    }

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
            authenticationStateProviderManager: AuthenticationStateProviderManager
        ): AuthenticationProviderManagerImpl {
            var authenticatorProviderManager = authenticationProviderManagerInstance.get()
            if (authenticatorProviderManager == null) {
                authenticatorProviderManager = AuthenticationProviderManagerImpl(
                    authenticationCore,
                    authenticationStateProviderManager
                )
                authenticationProviderManagerInstance = WeakReference(authenticatorProviderManager)
            }
            return authenticatorProviderManager
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
    override suspend fun isLoggedIn(context: Context): Boolean {
        return authenticationCore.validateAccessToken(context) ?: false
    }

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
            authenticatorsInThisStep =
                authenticationStateProviderManager.handleAuthenticationFlowResult(
                    it!!,
                    context
                )
        }.onFailure {
            authenticationStateProviderManager.emitAuthenticationState(AuthenticationState.Error(it))
        }
    }

    /**
     * Complete the deferred objects.
     */
    private fun completeDeferred() {
        if (!redirectAuthenticationResultDeferred.isCompleted) {
            // Complete the deferred object and finish the [authenticateWithRedirectUri] method
            redirectAuthenticationResultDeferred.complete(Unit)
        }

        if (!googleAuthenticationResultDeferred.isCompleted) {
            // Complete the deferred object and finish the [authenticateWithGoogle] method
            googleAuthenticationResultDeferred.complete(Unit)
        }
    }

    private suspend fun authenticateWithAuthenticator(
        authenticatorTypeString: String? = null,
        authenticatorIdString: String? = null,
        afterGetAuthenticatorType: suspend (AuthenticatorType) -> Unit
    ) {
        authenticationStateProviderManager.emitAuthenticationState(AuthenticationState.Loading)

        var authenticatorType: AuthenticatorType? =
            AuthenticatorTypeUtil.getAuthenticatorTypeFromAuthenticatorTypeList(
                authenticatorsInThisStep!!,
                authenticatorIdString,
                authenticatorTypeString
            ) ?: selectedAuthenticator

        if (authenticatorType != null) {
            runCatching {
                authenticationCore.getDetailsOfAuthenticatorType(authenticatorType)
            }.onSuccess {
                if (it != null) {
                    afterGetAuthenticatorType(it)
                } else {
                    authenticationStateProviderManager.emitAuthenticationState(
                        AuthenticationState.Error(
                            AuthenticatorProviderException(
                                AuthenticatorProviderException.AUTHENTICATOR_NOT_FOUND
                            )
                        )
                    )

                    selectedAuthenticator = null
                }
            }.onFailure {
                authenticationStateProviderManager.emitAuthenticationState(
                    AuthenticationState.Error(it)
                )

                selectedAuthenticator = null
            }
        } else {
            authenticationStateProviderManager.emitAuthenticationState(
                AuthenticationState.Error(
                    AuthenticatorProviderException(
                        AuthenticatorProviderException.AUTHENTICATOR_NOT_FOUND
                    )
                )
            )
        }
    }

    /**
     * Common function in all authenticate methods
     *
     * @param context The context of the application
     * @param userSelectedAuthenticatorType The selected authenticator type
     * @param authParams The authentication parameters of the selected authenticator
     * @param authParamsAsMap The authentication parameters of the selected authenticator as a LinkedHashMap<String, String>
     * with the key as the parameter name and the value as the parameter value
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    private suspend fun commonAuthenticate(
        context: Context,
        userSelectedAuthenticatorType: AuthenticatorType? = null,
        authParams: AuthParams? = null,
        authParamsAsMap: LinkedHashMap<String, String>? = null
    ) {
        authenticationStateProviderManager.emitAuthenticationState(AuthenticationState.Loading)

        // setting up the authenticator type
        var authenticatorType: AuthenticatorType? =
            userSelectedAuthenticatorType ?: selectedAuthenticator

        if (authenticatorType != null) {
            selectedAuthenticator = authenticatorType

            var authParamsMap: LinkedHashMap<String, String>? = authParamsAsMap

            if (authParams != null) {
                authParamsMap = authParams.getParameterBodyAuthenticator(
                    authenticatorType.requiredParams!!
                )
            }

            runCatching {
                authenticationCore.authenticate(authenticatorType, authParamsMap!!)
            }.onSuccess {
                authenticatorsInThisStep =
                    authenticationStateProviderManager.handleAuthenticationFlowResult(it!!, context)
                completeDeferred()
            }.onFailure {
                authenticationStateProviderManager.emitAuthenticationState(
                    AuthenticationState.Error(it)
                )
                completeDeferred()
            }

            selectedAuthenticator = null
        } else {
            authenticationStateProviderManager.emitAuthenticationState(
                AuthenticationState.Error(
                    AuthenticatorProviderException(
                        AuthenticatorProviderException.AUTHENTICATOR_NOT_FOUND
                    )
                )
            )

            selectedAuthenticator = null

            completeDeferred()
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
        authenticateWithAuthenticator(
            authenticatorTypeString = AuthenticatorTypes.BASIC_AUTHENTICATOR.authenticatorType
        ) {
            commonAuthenticate(
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
        authenticateWithAuthenticator(
            authenticatorTypeString = AuthenticatorTypes.TOTP_AUTHENTICATOR.authenticatorType
        ) {
            commonAuthenticate(
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
        authenticateWithAuthenticator(
            authenticatorTypeString = authenticatorTypeString,
            authenticatorIdString = authenticatorIdString
        ) {
            // Retrieving the prompt type of the authenticator
            val promptType: String? = it.metadata?.promptType

            if (promptType == PromptTypes.REDIRECTION_PROMPT.promptType) {
                // Retrieving the redirect URI of the authenticator
                val redirectUri: String? = it.metadata?.additionalData?.redirectUrl

                if (redirectUri.isNullOrEmpty()) {
                    authenticationStateProviderManager.emitAuthenticationState(
                        AuthenticationState.Error(
                            AuthenticatorProviderException(
                                AuthenticatorProviderException.REDIRECT_URI_NOT_FOUND
                            )
                        )
                    )

                    selectedAuthenticator = null
                } else {
                    selectedAuthenticator = it
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(redirectUri))
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)

                    redirectAuthenticationResultDeferred.await()
                }
            } else {
                authenticationStateProviderManager.emitAuthenticationState(
                    AuthenticationState.Error(
                        AuthenticatorProviderException(
                            AuthenticatorProviderException.NOT_REDIRECT_PROMPT
                        )
                    )
                )

                selectedAuthenticator = null
            }
        }
    }

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
    override suspend fun handleRedirectUri(context: Context, deepLink: Uri) {
        // Setting up the deferred object to wait for the result
        if (selectedAuthenticator != null) {
            val requiredParams: List<String> = selectedAuthenticator!!.requiredParams!!

            // Extract required parameters from the authenticator type
            val authParamsMap: LinkedHashMap<String, String> = LinkedHashMap()

            for (param in requiredParams) {
                val paramValue: String? = deepLink.getQueryParameter(param)

                if (paramValue != null) {
                    authParamsMap[param] = paramValue
                }
            }

            // Finish the [RedirectUriReceiverActivity] activity
            if (context is ComponentActivity) {
                context.finish()
            }

            commonAuthenticate(context, authParamsAsMap = authParamsMap)
        } else {
            authenticationStateProviderManager.emitAuthenticationState(
                AuthenticationState.Error(
                    AuthenticatorProviderException(
                        AuthenticatorProviderException.AUTHENTICATOR_NOT_FOUND
                    )
                )
            )
            // Complete the deferred object and finish the [authenticateWithRedirectUri] method
            redirectAuthenticationResultDeferred.complete(Unit)
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
     * Authenticate the user with the Google authenticator.
     *
     * @param context The context of the application
     * @param googleAuthenticateResultLauncher The [ActivityResultLauncher] object to handle the Google authentication result
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    override suspend fun authenticateWithGoogle(
        context: Context,
        googleAuthenticateResultLauncher: ActivityResultLauncher<Intent>
    ) {

        authenticateWithAuthenticator(
            authenticatorTypeString = AuthenticatorTypes.GOOGLE_AUTHENTICATOR.authenticatorType
        ) {

            // Get the Google Web Client ID
            val googleWebClientId: String? =
                authenticationCore.getAuthenticationCoreConfig().getGoogleWebClientId()

            if (googleWebClientId.isNullOrEmpty()) {
                authenticationStateProviderManager.emitAuthenticationState(
                    AuthenticationState.Error(
                        AuthenticatorProviderException(
                            AuthenticatorProviderException.GOOGLE_WEB_CLIENT_ID_NOT_FOUND
                        )
                    )
                )

               selectedAuthenticator = null
            } else {
                selectedAuthenticator = it

                val googleSignInClient = GoogleSignIn.getClient(
                    context,
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestServerAuthCode(googleWebClientId)
                        .requestIdToken(googleWebClientId)
                        .requestEmail()
                        .build()
                )
                val signInIntent = googleSignInClient.signInIntent

                googleAuthenticateResultLauncher.launch(signInIntent)

                googleAuthenticationResultDeferred.await()
            }
        }
    }

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
    override suspend fun handleGoogleAuthenticateResult(
        context: Context,
        result: ActivityResult
    ) {
        if (result.resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(
                result.data
            )
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                val idToken: String? = account.idToken
                val authCode: String? = account.serverAuthCode

                if (idToken.isNullOrEmpty() || authCode.isNullOrEmpty()) {
                    authenticationStateProviderManager.emitAuthenticationState(
                        AuthenticationState.Error(
                            AuthenticatorProviderException(
                                AuthenticatorProviderException.GOOGLE_AUTH_CODE_OR_ID_TOKEN_NOT_FOUND
                            )
                        )
                    )

                    selectedAuthenticator = null
                } else {
                    commonAuthenticate(
                        context,
                        authParams = GoogleNativeAuthenticatorTypeAuthParams(
                            accessToken = authCode,
                            idToken = idToken
                        )
                    )
                }
            } catch (e: ApiException) {
                authenticationStateProviderManager.emitAuthenticationState(
                    AuthenticationState.Error(e)
                )
                selectedAuthenticator = null

                // Complete the deferred object and finish the [authenticateWithGoogle] method
                googleAuthenticationResultDeferred.complete(Unit)
            }
        }
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
        authenticateWithAuthenticator(
            authenticatorIdString = authenticatorId
        ) {
            commonAuthenticate(
                context,
                userSelectedAuthenticatorType = it,
                authParamsAsMap = authParams
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
