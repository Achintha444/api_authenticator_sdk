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
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowNotSuccess
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlowSuccess
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticatorTypeException
import io.wso2.android.api_authenticator.sdk.models.flow_status.FlowStatus
import io.wso2.android.api_authenticator.sdk.models.state.AuthenticationState
import io.wso2.android.api_authenticator.sdk.models.state.TokenState
import io.wso2.android.api_authenticator.sdk.providers.di.AuthenticationProviderContainer
import io.wso2.android.api_authenticator.sdk.providers.util.AuthenticatorProviderUtil
import kotlinx.coroutines.flow.MutableStateFlow
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
     * Handle the state when the authenticator type is not found
     *
     * @param authenticators List of authenticators
     * @param authenticatorTypeString Authenticator type string
     *
     * @return Boolean value whether the authenticator type is not found
     * `true` if the authenticator type is not found, `false` otherwise
     */
    private fun handleStateWhenAuthenticatorTypeIsNotFound(
        authenticators: ArrayList<AuthenticatorType>,
        authenticatorTypeString: String
    ): AuthenticatorType? {
        val authenticatorType: AuthenticatorType? =
            AuthenticatorProviderUtil.getAuthenticatorTypeFromAuthenticatorTypeList(
                authenticators,
                authenticatorTypeString
            )

        if (authenticatorType == null) {
            authenticationStateHandler.emitAuthenticationState(
                AuthenticationState.Error(
                    AuthenticatorTypeException(
                        AuthenticatorTypeException.AUTHENTICATOR_NOT_FOUND_OR_MORE_THAN_ONE,
                        authenticatorTypeString
                    )
                )
            )
            return null
        } else {
            return authenticatorType
        }
    }

    /**
     * Common function in all authenticate methods
     *
     * @param authenticatorTypeString Authenticator type string
     * @param authParams [AuthParams] object
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    private suspend fun commonAuthenticate(
        context: Context,
        authenticatorTypeString: String,
        authParams: AuthParams
    ) {
        authenticationStateHandler.emitAuthenticationState(AuthenticationState.Loading)

        val authenticatorType: AuthenticatorType? =
            handleStateWhenAuthenticatorTypeIsNotFound(
                authenticatorsInThisStep!!,
                authenticatorTypeString
            )

        if (authenticatorType != null) {
            runCatching {
                authenticationCore.authenticate(
                    authenticatorType,
                    authParams
                )
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
    }

    /**
     * Authenticate the user with the username and password.
     *
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
            BasicAuthenticatorType.AUTHENTICATOR_TYPE,
            BasicAuthenticatorAuthParams(username, password)
        )
    }

    /**
     * Authenticate the user with the TOTP token.
     *
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
            TotpAuthenticatorType.AUTHENTICATOR_TYPE,
            TotpAuthenticatorTypeAuthParams(token)
        )
    }

    /**
     * Logout the user from the application.
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