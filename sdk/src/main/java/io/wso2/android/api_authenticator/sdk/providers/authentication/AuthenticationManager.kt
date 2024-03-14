package io.wso2.android.api_authenticator.sdk.providers.authentication

import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.managers.authenticator.AuthenticatorManager
import io.wso2.android.api_authenticator.sdk.core.managers.authn.AuthnManager
import io.wso2.android.api_authenticator.sdk.models.auth_params.BasicAuthenticatorAuthParams
import io.wso2.android.api_authenticator.sdk.models.auth_params.TotpAuthenticatorTypeAuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.providers.di.AuthenticationManagerContainer
import io.wso2.android.api_authenticator.sdk.providers.util.AuthenticationManagerUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference

/**
 * Authentication manager class that is used to manage the authentication process.
 *
 * @property authenticationCoreConfig [AuthenticationCoreConfig] object
 */
class AuthenticationManager private constructor(
    private val authenticationCoreConfig: AuthenticationCoreConfig
) {
    /**
     * Instance of the [AuthnManager] that will be used throughout the application
     */
    private var authenticationCore: AuthenticationCoreDef =
        AuthenticationManagerContainer.getAuthenticationCoreDef(
            authenticationCoreConfig
        )

    private val _authStateFlow = MutableStateFlow<AuthenticationState>(AuthenticationState.Initial)

    /**
     * Flow of the authentication state which is exposed to the outside.
     */
    val authenticationStateFlow: SharedFlow<AuthenticationState> = _authStateFlow.asSharedFlow()

    companion object {
        /**
         * Instance of the [AuthenticationManager] that will be used throughout the application
         */
        private var authenticationManagerInstance: WeakReference<AuthenticationManager> =
            WeakReference(null)

        /**
         * Initialize the [AuthenticationManager] instance and return the instance.
         *
         * @param authenticationCoreConfig The [AuthenticatorManager] instance
         */
        fun getInstance(
            authenticationCoreConfig: AuthenticationCoreConfig
        ): AuthenticationManager {
            var authenticatorManager = authenticationManagerInstance.get()
            if (authenticatorManager == null) {
                authenticatorManager = AuthenticationManager(authenticationCoreConfig)
                authenticationManagerInstance = WeakReference(authenticatorManager)
            }
            return authenticatorManager
        }
    }

    /**
     * Initialize the authentication process.
     * This method will initialize the authentication process and emit the state of the authentication process.
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Unauthorized] - The user is not authorized to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    suspend fun initializeAuthentication() {
        _authStateFlow.tryEmit(AuthenticationState.Loading)

        runBlocking {
            runCatching {
                authenticationCore.authorize()
            }.onSuccess {
                _authStateFlow.tryEmit(AuthenticationState.Unauthorized(it))
            }.onFailure {
                _authStateFlow.tryEmit(AuthenticationState.Error(it))
            }
        }
    }

    /**
     * Authenticate the user with the username and password.
     *
     * @param authenticatorType The [AuthenticatorType] of the authenticator
     * @param username The username of the user
     * @param password The password of the user
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Authorized] - The user is authorized to access the application
     * emit: [AuthenticationState.Unauthorized] - The user is not authorized to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    suspend fun authenticateWithUsernameAndPassword(
        authenticatorType: AuthenticatorType,
        username: String,
        password: String
    ) {
        _authStateFlow.tryEmit(AuthenticationState.Loading)
        runBlocking {
            runCatching {
                authenticationCore.authenticate(
                    authenticatorType,
                    BasicAuthenticatorAuthParams(
                        username,
                        password
                    )
                )
            }.onSuccess {
                AuthenticationManagerUtil.emitSuccessStateOnFlowStatus(it!!, _authStateFlow)
            }.onFailure {
                _authStateFlow.tryEmit(AuthenticationState.Error(it))
            }
        }
    }

    /**
     * Authenticate the user with the TOTP token.
     *
     * @param authenticatorType The [AuthenticatorType] of the authenticator
     * @param token The TOTP token of the user
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Authorized] - The user is authorized to access the application
     * emit: [AuthenticationState.Unauthorized] - The user is not authorized to access the application
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    suspend fun authenticateWithTotp(
        authenticatorType: AuthenticatorType,
        token: String
    ) {
        _authStateFlow.tryEmit(AuthenticationState.Loading)
        runBlocking {
            runCatching {
                authenticationCore.authenticate(
                    authenticatorType,
                    TotpAuthenticatorTypeAuthParams(token)
                )
            }.onSuccess {
                AuthenticationManagerUtil.emitSuccessStateOnFlowStatus(it!!, _authStateFlow)
            }.onFailure {
                _authStateFlow.tryEmit(AuthenticationState.Error(it))
            }
        }
    }
}