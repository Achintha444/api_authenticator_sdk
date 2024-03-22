package io.wso2.android.api_authenticator.sdk.providers.authentication_provider

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.managers.authenticator.AuthenticatorManager
import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.auth_params.BasicAuthenticatorAuthParams
import io.wso2.android.api_authenticator.sdk.models.auth_params.TotpAuthenticatorTypeAuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorTypes
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticatorProviderException
import io.wso2.android.api_authenticator.sdk.models.prompt_type.PromptTypes
import io.wso2.android.api_authenticator.sdk.models.state.AuthenticationState
import io.wso2.android.api_authenticator.sdk.providers.di.AuthenticationProviderContainer
import io.wso2.android.api_authenticator.sdk.providers.util.AuthenticatorProviderUtil
import kotlinx.coroutines.CompletableDeferred
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
    private var authenticationCore: AuthenticationCoreDef =
        AuthenticationProviderContainer.getAuthenticationCoreDef(
            authenticationCoreConfig
        )

    /**
     * Instance of the [AuthenticationStateHandler] that will be used throughout the application
     */
    private val authenticationStateHandler: AuthenticationStateHandler =
        AuthenticationProviderContainer.getAuthenticationStateHandler()

    /**
     * List of authenticators in this step of the authentication flow.
     */
    private var authenticatorsInThisStep: ArrayList<AuthenticatorType>? = null

    /**
     * The selected authenticator for the authentication process.
     */
    private var selectedAuthenticator: AuthenticatorType? = null

    /**
     * Deferred object to wait for the result of the authentication process.
     */
    private val redirectAuthenticationResultDeferred: CompletableDeferred<Unit> = CompletableDeferred()

    /**
     * Flow of the authentication state which is exposed to the outside.
     */
    val authenticationStateFlow: SharedFlow<AuthenticationState> =
        authenticationStateHandler.authenticationStateFlow

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
        fun getInstance(): AuthenticationProvider? {
            return authenticationProviderInstance.get()
        }
    }

    /**
     * Check whether the user is logged in or not.
     *
     * @return `true` if the user is logged in, `false` otherwise
     */
    suspend fun isLoggedIn(context: Context): Boolean {
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
    suspend fun isLoggedInStateFlow(context: Context) {
        authenticationStateHandler.emitAuthenticationState(AuthenticationState.Loading)

        // TODO: Remove this block
        authenticationCore.clearTokens(context)

        runCatching {
            authenticationCore.validateAccessToken(context)
        }.onSuccess { isAccessTokenValid ->
            if (isAccessTokenValid == true) {
                authenticationStateHandler.emitAuthenticationState(
                    AuthenticationState.Authenticated
                )
            } else {
                authenticationStateHandler.emitAuthenticationState(AuthenticationState.Initial)
            }
        }.onFailure {
            authenticationStateHandler.emitAuthenticationState(AuthenticationState.Initial)
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
    suspend fun initializeAuthentication(context: Context) {
        authenticationStateHandler.emitAuthenticationState(AuthenticationState.Loading)

        runCatching {
            authenticationCore.authorize()
        }.onSuccess {
            authenticatorsInThisStep = authenticationStateHandler.handleAuthenticationFlowResult(
                it!!,
                context,
                authenticationCore::exchangeAuthorizationCode,
                authenticationCore::saveTokenState
            )
        }.onFailure {
            authenticationStateHandler.emitAuthenticationState(AuthenticationState.Error(it))
        }
    }

    /**
     * Get the authenticator type from the authenticator type list.
     * Done by checking the authenticator id or authenticator type.
     *
     * Precedence: authenticatorId > authenticatorType
     *
     * @param authenticatorIdString The authenticator id string
     * @param authenticatorTypeString The authenticator type string
     */
    private fun getAuthenticatorTypeFromAuthenticatorTypeList(
        authenticatorIdString: String? = null,
        authenticatorTypeString: String? = null
    ): AuthenticatorType? {
        // setting up the authenticator type
        var authenticatorType: AuthenticatorType? = null

        if (authenticatorIdString != null) {
            authenticatorType =
                AuthenticatorProviderUtil
                    .getAuthenticatorTypeFromAuthenticatorTypeListOnAuthenticatorId(
                        authenticatorsInThisStep!!,
                        authenticatorIdString
                    )
        } else if (authenticatorTypeString != null) {
            authenticatorType =
                AuthenticatorProviderUtil.getAuthenticatorTypeFromAuthenticatorTypeList(
                    authenticatorsInThisStep!!,
                    authenticatorTypeString
                )
        }

        return authenticatorType
    }

    /**
     * Common function in all authenticate methods
     *
     * @param context The context of the application
     * @param authenticatorTypeString The authenticator type string
     * @param authenticatorIdString The authenticator id string
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
        authenticatorTypeString: String? = null,
        authenticatorIdString: String? = null,
        authParams: AuthParams? = null,
        authParamsAsMap: LinkedHashMap<String, String>? = null
    ) {
        authenticationStateHandler.emitAuthenticationState(AuthenticationState.Loading)

        // setting up the authenticator type
        var authenticatorType: AuthenticatorType? = getAuthenticatorTypeFromAuthenticatorTypeList(
            authenticatorIdString,
            authenticatorTypeString
        )

        if (authenticatorType != null) {
            selectedAuthenticator = authenticatorType

            var authParamsMap: LinkedHashMap<String, String>? = authParamsAsMap

            if (authParams != null) {
                authParamsMap = authParams.getParameterBodyAuthenticator(
                    authenticatorType.requiredParams!!
                )
            }

            runCatching {
                authenticationCore.authenticate(
                    authenticatorType,
                    authParamsMap!!
                )
            }.onSuccess {
                authenticatorsInThisStep =
                    authenticationStateHandler.handleAuthenticationFlowResult(
                        it!!,
                        context,
                        authenticationCore::exchangeAuthorizationCode,
                        authenticationCore::saveTokenState
                    )
                selectedAuthenticator = null
            }.onFailure {
                authenticationStateHandler.emitAuthenticationState(AuthenticationState.Error(it))
                selectedAuthenticator = null
            }
        } else {
            authenticationStateHandler.emitAuthenticationState(
                AuthenticationState.Error(
                    AuthenticatorProviderException(
                        AuthenticatorProviderException.AUTHENTICATOR_NOT_FOUND
                    )
                )
            )
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
    suspend fun authenticateWithUsernameAndPassword(
        context: Context,
        username: String,
        password: String
    ) {
        commonAuthenticate(
            context,
            authenticatorTypeString = AuthenticatorTypes.BASIC_AUTHENTICATOR.authenticatorType,
            authParams = BasicAuthenticatorAuthParams(username, password)
        )
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
    suspend fun authenticateWithTotp(context: Context, token: String) {
        commonAuthenticate(
            context,
            authenticatorTypeString = AuthenticatorTypes.TOTP_AUTHENTICATOR.authenticatorType,
            authParams = TotpAuthenticatorTypeAuthParams(token)
        )
    }

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
    ) {
        // Setting up the deferred object to wait for the result
        authenticationStateHandler.emitAuthenticationState(AuthenticationState.Loading)

        // setting up the authenticator type
        var authenticatorType: AuthenticatorType? = getAuthenticatorTypeFromAuthenticatorTypeList(
            authenticatorIdString = authenticatorId,
            authenticatorTypeString = authenticatorType
        )

        // Retrieving the prompt type of the authenticator
        val promptType: String? = authenticatorType?.metadata?.promptType

        if (promptType == PromptTypes.REDIRECTION_PROMPT.promptType) {
            // Retrieving the redirect URI of the authenticator
            val redirectUri: String? = authenticatorType?.metadata?.additionalData?.redirectUrl

            if (redirectUri.isNullOrEmpty()) {
                authenticationStateHandler.emitAuthenticationState(
                    AuthenticationState.Error(
                        AuthenticatorProviderException(
                            AuthenticatorProviderException.REDIRECT_URI_NOT_FOUND
                        )
                    )
                )
            } else {
                selectedAuthenticator = authenticatorType
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(redirectUri))
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)

                redirectAuthenticationResultDeferred.await()
            }
        } else {
            authenticationStateHandler.emitAuthenticationState(
                AuthenticationState.Error(
                    AuthenticatorProviderException(
                        AuthenticatorProviderException.NOT_REDIRECT_PROMPT
                    )
                )
            )
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
    internal suspend fun handleRedirectUri(context: Context, deepLink: Uri) {
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

            // Authenticate the user with the selected authenticator
            runCatching {
                authenticationCore.authenticate(
                    selectedAuthenticator!!,
                    authParamsMap
                )
            }.onSuccess {
                authenticatorsInThisStep =
                    authenticationStateHandler.handleAuthenticationFlowResult(
                        it!!,
                        context,
                        authenticationCore::exchangeAuthorizationCode,
                        authenticationCore::saveTokenState
                    )
                selectedAuthenticator = null
                // Complete the deferred object and finish the [authenticateWithRedirectUri] method
                redirectAuthenticationResultDeferred.complete(Unit)
            }.onFailure {
                authenticationStateHandler.emitAuthenticationState(AuthenticationState.Error(it))
                // Complete the deferred object and finish the [authenticateWithRedirectUri] method
                selectedAuthenticator = null
                redirectAuthenticationResultDeferred.complete(Unit)
            }
        } else {
            authenticationStateHandler.emitAuthenticationState(
                AuthenticationState.Error(
                    AuthenticatorProviderException(
                        AuthenticatorProviderException.AUTHENTICATOR_NOT_FOUND
                    )
                )
            )
            selectedAuthenticator = null
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
    suspend fun authenticateWithOpenIdConnect(context: Context) {
        authenticateWithRedirectUri(
            context,
            authenticatorType = AuthenticatorTypes.OPENID_CONNECT_AUTHENTICATOR.authenticatorType
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
    suspend fun authenticateWithAnyAuthenticator(
        context: Context,
        authenticatorId: String,
        authParams: LinkedHashMap<String, String>
    ) {
        commonAuthenticate(
            context,
            authenticatorIdString = authenticatorId,
            authParamsAsMap = authParams
        )
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
    suspend fun logout(context: Context) {
        authenticationStateHandler.emitAuthenticationState(AuthenticationState.Loading)

        runCatching {
            val clientId: String = authenticationCoreConfig.getClientId()
            val idToken: String? = authenticationCore.getIDToken(context)
            // Call the logout endpoint
            authenticationCore.logout(
                clientId,
                idToken!!
            )

            // clear the tokens
            authenticationCore.clearTokens(context)
        }.onSuccess {
            authenticationStateHandler.emitAuthenticationState(AuthenticationState.Initial)
        }.onFailure {
            authenticationStateHandler.emitAuthenticationState(AuthenticationState.Error(it))
        }
    }
}