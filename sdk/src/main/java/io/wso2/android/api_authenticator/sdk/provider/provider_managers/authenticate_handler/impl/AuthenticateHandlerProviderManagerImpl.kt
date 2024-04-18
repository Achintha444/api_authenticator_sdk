package io.wso2.android.api_authenticator.sdk.provider.provider_managers.authenticate_handler.impl

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import io.wso2.android.api_authenticator.sdk.core.core_types.authentication.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.core_types.native_authentication_handler.NativeAuthenticationHandlerCoreDef
import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator.Authenticator
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticatorProviderException
import io.wso2.android.api_authenticator.sdk.models.exceptions.GoogleNativeAuthenticationException
import io.wso2.android.api_authenticator.sdk.models.exceptions.PasskeyAuthenticationException
import io.wso2.android.api_authenticator.sdk.models.exceptions.RedirectAuthenticationException
import io.wso2.android.api_authenticator.sdk.models.state.AuthenticationState
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authenticate_handler.AuthenticateHandlerProviderManager
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication_state.AuthenticationStateProviderManager
import io.wso2.android.api_authenticator.sdk.util.AuthenticatorUtil
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
    private var authenticatorsInThisStep: ArrayList<Authenticator>? = null

    /**
     * The selected authenticator for the authentication process.
     */
    private var selectedAuthenticator: Authenticator? = null

    /**
     * Set the authenticators in this step of the authentication flow.
     *
     * @param authenticatorsInThisStep The list of authenticators in this step
     */
    override fun setAuthenticatorsInThisStep(
        authenticatorsInThisStep: ArrayList<Authenticator>?
    ) {
        this.authenticatorsInThisStep = authenticatorsInThisStep
    }

    /**
     * Authenticate the user with the selected authenticator . This method is used to
     * get the full details of the selected authenticator , then perform the passed
     * authentication process.
     *
     * @param authenticatorId The authenticator ID string
     * @param authenticatorTypeString The authenticator type string
     * @param afterGetAuthenticator The function to be executed after getting the authenticator
     *
     * emit: [AuthenticationState.Loading] - The application is in the process of loading the authentication state
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    override suspend fun authenticateWithAuthenticator(
        authenticatorId: String,
        authenticatorTypeString: String,
        afterGetAuthenticator: suspend (Authenticator) -> Unit
    ) {
        authenticationStateProviderManager.emitAuthenticationState(AuthenticationState.Loading)

        val authenticator: Authenticator? =
            AuthenticatorUtil.getAuthenticatorFromAuthenticatorsList(
                authenticatorsInThisStep!!,
                authenticatorId,
                authenticatorTypeString
            ) ?: selectedAuthenticator

        if (authenticator != null) {
            runCatching {
                authenticationCore.getDetailsOfAuthenticator(authenticator)
            }.onSuccess {
                selectedAuthenticator = it
                if (it != null) {
                    afterGetAuthenticator(it)
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
     * @param userSelectedAuthenticator The selected authenticator
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
        userSelectedAuthenticator: Authenticator?,
        authParams: AuthParams?,
        authParamsAsMap: LinkedHashMap<String, String>?
    ) {
        authenticationStateProviderManager.emitAuthenticationState(AuthenticationState.Loading)

        // setting up the authenticator
        val authenticator: Authenticator? =
            userSelectedAuthenticator ?: selectedAuthenticator

        if (authenticator != null) {
            selectedAuthenticator = authenticator

            var authParamsMap: LinkedHashMap<String, String>? = authParamsAsMap

            if (authParams != null) {
                authParamsMap = authParams.getParameterBodyAuthenticator(
                    authenticator.requiredParams!!
                )
            }

            runCatching {
                authenticationCore.authn(authenticator, authParamsMap!!)
            }.onSuccess {
                authenticatorsInThisStep =
                    authenticationStateProviderManager.handleAuthenticationFlowResult(it!!, context)
            }.onFailure {
                authenticationStateProviderManager.emitAuthenticationState(
                    AuthenticationState.Error(it)
                )
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

        selectedAuthenticator = null
    }

    /**
     * Redirect the user to the authenticator's authentication page.
     *
     * @param context The context of the application
     * @param authenticator The authenticator to redirect the user
     *
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     */
    override suspend fun redirectAuthenticate(
        context: Context,
        authenticator: Authenticator
    ) {
        runCatching {
            nativeAuthenticationHandlerCore.handleRedirectAuthentication(context, authenticator)
        }.onSuccess {
            val authParamsMap: LinkedHashMap<String, String>? = it

            if (authParamsMap.isNullOrEmpty()) {
                authenticationStateProviderManager.emitAuthenticationState(
                    AuthenticationState.Error(
                        RedirectAuthenticationException(
                            RedirectAuthenticationException.AUTHENTICATION_PARAMS_NOT_FOUND
                        )
                    )
                )

                selectedAuthenticator = null
            } else {
                commonAuthenticate(context, authParamsAsMap = authParamsMap)
            }
        }.onFailure {
            authenticationStateProviderManager.emitAuthenticationState(
                AuthenticationState.Error(it)
            )
            selectedAuthenticator = null
        }
    }

    /**
     * Authenticate the user with the Google authenticator using Credential Manager API.
     *
     * @param context The context of the application
     * @param nonce The nonce value to authenticate the user, which is sent by the Identity Server
     *
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun googleAuthenticate(context: Context, nonce: String) {
        runCatching {
            nativeAuthenticationHandlerCore.handleGoogleNativeAuthentication(context, nonce)
        }.onSuccess {
            if (it == null) {
                authenticationStateProviderManager.emitAuthenticationState(
                    AuthenticationState.Error(
                        GoogleNativeAuthenticationException(
                            GoogleNativeAuthenticationException.GOOGLE_ID_TOKEN_NOT_FOUND
                        )
                    )
                )

                selectedAuthenticator = null
            } else {
                commonAuthenticate(context, authParams = it)
            }
        }.onFailure {
            authenticationStateProviderManager.emitAuthenticationState(
                AuthenticationState.Error(it)
            )
            selectedAuthenticator = null
        }
    }

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
        runCatching {
            nativeAuthenticationHandlerCore.handleGoogleNativeLegacyAuthenticateResult(
                resultCode,
                data
            )
        }.onSuccess {
            if (it == null) {
                authenticationStateProviderManager.emitAuthenticationState(
                    AuthenticationState.Error(
                        GoogleNativeAuthenticationException(
                            GoogleNativeAuthenticationException
                                .GOOGLE_AUTH_CODE_OR_ID_TOKEN_NOT_FOUND
                        )
                    )
                )

                selectedAuthenticator = null
            } else {
                commonAuthenticate(context, authParams = it)
            }
        }.onFailure {
            authenticationStateProviderManager.emitAuthenticationState(
                AuthenticationState.Error(it)
            )
            selectedAuthenticator = null
        }
    }

    /**
     * Authenticate the user with the Passkey authenticator using Credential Manager API.
     *
     * @param context The context of the application
     * @param authenticator The authenticator to authenticate the user
     * @param allowCredentials The list of allowed credentials. Default is empty array.
     * @param timeout The timeout for the authentication. Default is 300000.
     * @param userVerification The user verification method. Default is "required"
     *
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun passkeyAuthenticate(
        context: Context,
        authenticator: Authenticator,
        allowCredentials: List<String>?,
        timeout: Long?,
        userVerification: String?
    ) {
        runCatching {
            nativeAuthenticationHandlerCore.handlePasskeyAuthentication(
                context,
                authenticator.metadata?.additionalData?.challengeData,
                allowCredentials,
                timeout,
                userVerification
            )
        }.onSuccess {
            if (it == null) {
                authenticationStateProviderManager.emitAuthenticationState(
                    AuthenticationState.Error(
                        PasskeyAuthenticationException(
                            PasskeyAuthenticationException.PASSKEY_AUTHENTICATION_FAILED
                        )
                    )
                )

                selectedAuthenticator = null
            } else {
                commonAuthenticate(context, authParams = it)
            }
        }.onFailure {
            authenticationStateProviderManager.emitAuthenticationState(
                AuthenticationState.Error(it)
            )
            selectedAuthenticator = null
        }
    }
}
