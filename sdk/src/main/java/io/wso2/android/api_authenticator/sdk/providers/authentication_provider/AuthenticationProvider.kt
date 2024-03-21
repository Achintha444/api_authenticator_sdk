package io.wso2.android.api_authenticator.sdk.providers.authentication_provider

import android.content.Context
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.managers.authenticator.AuthenticatorManager
import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.auth_params.BasicAuthenticatorAuthParams
import io.wso2.android.api_authenticator.sdk.models.auth_params.TotpAuthenticatorTypeAuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.BasicAuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.TotpAuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.state.AuthenticationState
import io.wso2.android.api_authenticator.sdk.providers.di.AuthenticationProviderContainer
import io.wso2.android.api_authenticator.sdk.providers.util.AuthenticatorProviderUtil
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
        var authenticatorType: AuthenticatorType? = null

        if (authenticatorIdString != null) {
            authenticatorType =
                AuthenticatorProviderUtil.getAuthenticatorTypeFromAuthenticatorTypeList(
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

        if (authenticatorType != null) {
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
            }.onFailure {
                authenticationStateHandler.emitAuthenticationState(AuthenticationState.Error(it))
            }
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
            authenticatorTypeString = BasicAuthenticatorType.AUTHENTICATOR_TYPE,
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
            authenticatorTypeString = TotpAuthenticatorType.AUTHENTICATOR_TYPE,
            authParams = TotpAuthenticatorTypeAuthParams(token)
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