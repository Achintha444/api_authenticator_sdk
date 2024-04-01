package io.wso2.android.api_authenticator.sdk.provider.provider_managers.authenticate_handler.impl

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import io.wso2.android.api_authenticator.sdk.core.core_types.authentication.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.core_types.native_authentication_handler.NativeAuthenticationHandlerCoreDef
import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticatorProviderException
import io.wso2.android.api_authenticator.sdk.models.exceptions.GoogleNativeAuthenticationException
import io.wso2.android.api_authenticator.sdk.models.exceptions.PasskeyAuthenticationException
import io.wso2.android.api_authenticator.sdk.models.exceptions.RedirectAuthenticationException
import io.wso2.android.api_authenticator.sdk.models.state.AuthenticationState
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authenticate_handler.AuthenticateHandlerProviderManager
import io.wso2.android.api_authenticator.sdk.provider.provider_managers.authentication_state.AuthenticationStateProviderManager
import io.wso2.android.api_authenticator.sdk.util.AuthenticatorTypeUtil
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
        runCatching {
            nativeAuthenticationHandlerCore.handleRedirectAuthentication(context, authenticatorType)
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
     *
     * emit: [AuthenticationState.Error] - An error occurred during the authentication process
     * emit: [AuthenticationState.Authenticated] - The user is authenticated to access the application
     * emit: [AuthenticationState.Unauthenticated] - The user is not authenticated to access the application
     */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override suspend fun googleAuthenticate(context: Context) {
        runCatching {
            nativeAuthenticationHandlerCore.handleGoogleNativeAuthentication(context)
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
     * @param authenticatorType The authenticator type to authenticate the user
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
        authenticatorType: AuthenticatorType,
        allowCredentials: List<String>?,
        timeout: Long?,
        userVerification: String?
    ) {
        runCatching {
            nativeAuthenticationHandlerCore.handlePasskeyAuthentication(
                context,
                authenticatorType.metadata?.additionalData?.challengeData,
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
