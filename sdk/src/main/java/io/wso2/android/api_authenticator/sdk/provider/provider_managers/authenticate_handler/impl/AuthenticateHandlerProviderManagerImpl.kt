package io.wso2.android.api_authenticator.sdk.provider.provider_managers.authenticate_handler.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import io.wso2.android.api_authenticator.sdk.core.core_types.authentication.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.core_types.native_authentication_handler.NativeAuthenticationHandlerCoreDef
import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticatorProviderException
import io.wso2.android.api_authenticator.sdk.models.exceptions.GoogleNativeAuthenticationException
import io.wso2.android.api_authenticator.sdk.models.prompt_type.PromptTypes
import io.wso2.android.api_authenticator.sdk.models.state.AuthenticationState
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authenticate_handler.AuthenticateHandlerProviderManager
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication_state.AuthenticationStateProviderManager
import io.wso2.android.api_authenticator.sdk.util.AuthenticatorTypeUtil
import kotlinx.coroutines.CompletableDeferred
import java.lang.ref.WeakReference

/**
 * Implementation of the [AuthenticateHandlerProviderManager] interface.
 * This provider manager is responsible for handling the common authentication processes.
 *
 * @property authenticationCore The authentication core
 * @property authenticationStateProviderManager The authentication state provider manager
 */
class AuthenticateHandlerProviderManagerImpl private constructor(
    private val authenticationCore: AuthenticationCoreDef,
    private val nativeAuthenticationHandlerCore: NativeAuthenticationHandlerCoreDef,
    private val authenticationStateProviderManager: AuthenticationStateProviderManager,
) : AuthenticateHandlerProviderManager {

    companion object {
        /**
         * Instance of the [AuthenticateHandlerProviderManagerImpl] to use in the application
         */
        private var authenticateHandlerProviderManagerInstance: WeakReference<AuthenticateHandlerProviderManagerImpl> =
            WeakReference(null)

        /**
         * Initialize the [AuthenticateHandlerProviderManagerImpl] instance and return the instance.
         *
         * @param authenticationCore The [AuthenticationCoreDef] instance
         * @param nativeAuthenticationHandlerCore The [NativeAuthenticationHandlerCoreDef] instance
         * @param authenticationStateProviderManager The [AuthenticationStateProviderManager] instance
         *
         * @return The [AuthenticateHandlerProviderManagerImpl] instance
         */
        fun getInstance(
            authenticationCore: AuthenticationCoreDef,
            nativeAuthenticationHandlerCore: NativeAuthenticationHandlerCoreDef,
            authenticationStateProviderManager: AuthenticationStateProviderManager
        ): AuthenticateHandlerProviderManagerImpl {
            var authenticateHandlerProviderManager =
                authenticateHandlerProviderManagerInstance.get()
            if (authenticateHandlerProviderManager == null) {
                authenticateHandlerProviderManager = AuthenticateHandlerProviderManagerImpl(
                    authenticationCore,
                    nativeAuthenticationHandlerCore,
                    authenticationStateProviderManager
                )
                authenticateHandlerProviderManagerInstance =
                    WeakReference(authenticateHandlerProviderManager)
            }
            return authenticateHandlerProviderManager
        }

        /**
         * Get the instance of the [AuthenticateHandlerProviderManagerImpl].
         *
         * @return The [AuthenticateHandlerProviderManagerImpl] instance
         */
        fun getInstance(): AuthenticateHandlerProviderManagerImpl? =
            authenticateHandlerProviderManagerInstance.get()
    }

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
     *
     * TODO: Move to the Core module
     */
    private val redirectAuthenticationResultDeferred: CompletableDeferred<Unit> by lazy {
        CompletableDeferred()
    }

    /**
     * Complete the deferred objects.
     */
    private fun completeDeferred() {
        if (!redirectAuthenticationResultDeferred.isCompleted) {
            // Complete the deferred object and finish the [authenticateWithRedirectUri] method
            redirectAuthenticationResultDeferred.complete(Unit)
        }
    }

    /**
     * Set the authenticators in this step of the authentication flow.
     *
     * @param authenticatorsInThisStep The list of authenticators in this step
     */
    override fun setAuthenticatorsInThisStep(
        authenticatorsInThisStep: ArrayList<AuthenticatorType>?
    ) {
        this.authenticatorsInThisStep = authenticatorsInThisStep
    }

    /**
     * Authenticate the user with the selected authenticator type. This method is used to
     * get the full details of the selected authenticator type, then perform the passed
     * authentication process.
     *
     * @param authenticatorTypeString The authenticator type string
     * @param authenticatorIdString The authenticator ID string
     * @param afterGetAuthenticatorType The function to be executed after getting the authenticator type
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    override suspend fun authenticateWithAuthenticator(
        authenticatorTypeString: String?,
        authenticatorIdString: String?,
        afterGetAuthenticatorType: suspend (AuthenticatorType) -> Unit
    ) {
        authenticationStateProviderManager.emitAuthenticationState(AuthenticationState.Loading)

        val authenticatorType: AuthenticatorType? =
            AuthenticatorTypeUtil.getAuthenticatorTypeFromAuthenticatorTypeList(
                authenticatorsInThisStep!!,
                authenticatorIdString,
                authenticatorTypeString
            ) ?: selectedAuthenticator

        if (authenticatorType != null) {
            runCatching {
                authenticationCore.getDetailsOfAuthenticatorType(authenticatorType)
            }.onSuccess {
                selectedAuthenticator = it
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
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    override suspend fun commonAuthenticate(
        context: Context,
        userSelectedAuthenticatorType: AuthenticatorType?,
        authParams: AuthParams?,
        authParamsAsMap: LinkedHashMap<String, String>?
    ) {
        authenticationStateProviderManager.emitAuthenticationState(AuthenticationState.Loading)

        // setting up the authenticator type
        val authenticatorType: AuthenticatorType? =
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
            }.onFailure {
                authenticationStateProviderManager.emitAuthenticationState(
                    AuthenticationState.Error(it)
                )
            }

            completeDeferred()

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
     * Redirect the user to the authenticator's authentication page.
     *
     * @param context The context of the application
     * @param authenticatorType The authenticator type to redirect the user
     *
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    override suspend fun redirectAuthenticate(
        context: Context,
        authenticatorType: AuthenticatorType
    ) {
        // Retrieving the prompt type of the authenticator
        val promptType: String? = authenticatorType.metadata?.promptType

        if (promptType == PromptTypes.REDIRECTION_PROMPT.promptType) {
            // Retrieving the redirect URI of the authenticator
            val redirectUri: String? = authenticatorType.metadata?.additionalData?.redirectUrl

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
                selectedAuthenticator = authenticatorType

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
     * Authenticate the user with the Google authenticator using Credential Manager API.
     *
     * @param context The context of the application
     *
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun googleAuthenticate(context: Context): String? =
        nativeAuthenticationHandlerCore.handleGoogleNativeAuthentication(context)

    /**
     * Authenticate the user with the Google authenticator using the legacy one tap method.
     *
     * @param context The context of the application
     * @param googleAuthenticateResultLauncher The result launcher for the Google authentication process
     *
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    override suspend fun googleLegacyAuthenticate(
        context: Context,
        googleAuthenticateResultLauncher: ActivityResultLauncher<Intent>
    ) {
        runCatching {
            nativeAuthenticationHandlerCore.handleGoogleNativeLegacyAuthentication(
                context,
                googleAuthenticateResultLauncher
            )
        }.onFailure {
            authenticationStateProviderManager.emitAuthenticationState(
                AuthenticationState.Error(it)
            )
            selectedAuthenticator = null
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
    override suspend fun handleGoogleNativeLegacyAuthenticateResult(
        context: Context,
        result: ActivityResult
    ) {
        runCatching {
            nativeAuthenticationHandlerCore.handleGoogleNativeLegacyAuthenticateResult(result)
        }.onSuccess {
            val googleNativeLegacyAuthParams: LinkedHashMap<String, String>? = it

            if (googleNativeLegacyAuthParams.isNullOrEmpty()) {
                authenticationStateProviderManager.emitAuthenticationState(
                    AuthenticationState.Error(
                        GoogleNativeAuthenticationException(
                            GoogleNativeAuthenticationException.GOOGLE_AUTH_CODE_OR_ID_TOKEN_NOT_FOUND
                        )
                    )
                )

                selectedAuthenticator = null
            } else {
                commonAuthenticate(
                    context,
                    authParamsAsMap = googleNativeLegacyAuthParams
                )
            }
        }.onFailure {
            authenticationStateProviderManager.emitAuthenticationState(
                AuthenticationState.Error(it)
            )
            selectedAuthenticator = null
        }
    }
}
