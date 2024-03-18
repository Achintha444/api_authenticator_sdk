package io.wso2.android.api_authenticator.sdk.core.managers.app_auth.impl

import android.content.Context
import android.net.Uri
import io.wso2.android.api_authenticator.sdk.core.managers.app_auth.AppAuthManager
import io.wso2.android.api_authenticator.sdk.models.exceptions.AppAuthManagerException
import io.wso2.android.api_authenticator.sdk.models.http_client.CustomHttpURLConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.GrantTypeValues
import net.openid.appauth.TokenRequest
import net.openid.appauth.TokenResponse
import okhttp3.OkHttpClient
import java.lang.ref.WeakReference
import javax.net.ssl.X509TrustManager
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Use to manage the AppAuth SDK.
 *
 * @property customTrustClient The [OkHttpClient] instance.
 * @property clientId The client ID.
 * @property redirectUri The redirect URI.
 * @property serviceConfig The [AuthorizationServiceConfiguration] instance.
 */
internal class AppAuthManagerImpl private constructor(
    private val customTrustClient: OkHttpClient,
    private val clientId: String,
    private val redirectUri: Uri,
    private val serviceConfig: AuthorizationServiceConfiguration
) : AppAuthManager {
    companion object {
        /**
         * Instance of the [AppAuthManagerImpl] class.
         */
        private var appAuthManagerImplInstance = WeakReference<AppAuthManagerImpl?>(null)

        /**
         * Initialize the [AppAuthManagerImpl] class.
         *
         * @property httpBuilderClient The [OkHttpClient] instance.
         * @property clientId The client ID.
         * @property redirectUri The redirect URI.
         * @property serviceConfig The [AuthorizationServiceConfiguration] instance.
         *
         * @return The [AppAuthManagerImpl] instance.
         */
        fun getInstance(
            httpBuilderClient: OkHttpClient,
            clientId: String,
            redirectUri: Uri,
            serviceConfig: AuthorizationServiceConfiguration
        ): AppAuthManagerImpl {
            var appAuthManagerImpl = appAuthManagerImplInstance.get()
            if (appAuthManagerImpl == null) {
                appAuthManagerImpl = AppAuthManagerImpl(
                    httpBuilderClient,
                    clientId,
                    redirectUri,
                    serviceConfig
                )
                appAuthManagerImplInstance = WeakReference(appAuthManagerImpl)
            }
            return appAuthManagerImpl
        }
    }

    /**
     * The [AuthState] instance to keep track of the authorization state.
     */
    private var _appAuthState: AuthState? = null

    init {
        // Initialize the AuthState
        _appAuthState = AuthState(serviceConfig)
    }

    /**
     * Use to get the [AuthorizationService] instance to perform requests.
     *
     * @param context The [Context] instance.
     *
     * @return The [AuthorizationService] instance.
     */
    private fun getAuthorizationService(context: Context): AuthorizationService {
        return AuthorizationService(
            context,
            AppAuthConfiguration.Builder()
                .setConnectionBuilder { url ->
                    CustomHttpURLConnection(
                        url,
                        customTrustClient.x509TrustManager as X509TrustManager,
                        customTrustClient.sslSocketFactory
                    ).getConnection()
                }
                .build()
        )
    }

    /**
     * Use to get the [TokenRequest.Builder] instance.
     *
     * @return The [TokenRequest.Builder] instance.
     */
    private fun getTokenRequestBuilder(): TokenRequest.Builder {
        return TokenRequest.Builder(
            serviceConfig,
            clientId
        )
    }

    /**
     * Use to exchange the authorization code for the access token.
     *
     * @param authorizationCode The authorization code.
     *
     * @throws AppAuthManagerException If the token request fails.
     *
     * @return The token response [TokenResponse] instance.
     */
    override suspend fun exchangeAuthorizationCode(
        authorizationCode: String,
        context: Context,
    ): AuthState? = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            val tokenRequest: TokenRequest = getTokenRequestBuilder()
                .setAuthorizationCode(authorizationCode)
                .setClientId(clientId)
                .setRedirectUri(redirectUri)
                .build()

            val authService: AuthorizationService = getAuthorizationService(context)

            try {
                authService.performTokenRequest(tokenRequest) { tokenResponse, exception ->
                    // Update the AuthState
                    _appAuthState!!.update(tokenResponse, exception)

                    when {
                        exception != null -> {
                            continuation.resumeWithException(
                                AppAuthManagerException(
                                    AppAuthManagerException.TOKEN_REQUEST_FAILED,
                                    exception.message
                                )
                            )
                        }

                        tokenResponse == null -> {
                            continuation.resumeWithException(
                                AppAuthManagerException(
                                    AppAuthManagerException.EMPTY_TOKEN_RESPONSE
                                )
                            )
                        }

                        else -> {
                            continuation.resume(_appAuthState)
                        }
                    }
                    when {
                        exception != null -> {
                            continuation.resumeWithException(
                                AppAuthManagerException(
                                    AppAuthManagerException.TOKEN_REQUEST_FAILED,
                                    exception.message
                                )
                            )
                        }

                        tokenResponse == null -> {
                            continuation.resumeWithException(
                                AppAuthManagerException(
                                    AppAuthManagerException.EMPTY_TOKEN_RESPONSE
                                )
                            )
                        }

                        else -> {
                            continuation.resume(_appAuthState)
                        }
                    }
                }
            } catch (ex: Exception) {
                continuation.resumeWithException(
                    AppAuthManagerException(
                        AppAuthManagerException.TOKEN_REQUEST_FAILED,
                        ex.message
                    )
                )
            } finally {
                authService.dispose()
            }
        }
    }

    /**
     * Use to perform the refresh token grant.
     *
     * @param refreshToken The refresh token.
     * @param context The [Context] instance.
     *
     * @throws AppAuthManagerException If the token request fails.
     *
     * @return The [TokenResponse] instance.
     */
    override suspend fun performRefreshTokenGrant(
        refreshToken: String?,
        context: Context
    ): TokenResponse? = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            // Check we have a refresh token
            if (refreshToken.isNullOrBlank()) {
                continuation.resumeWithException(
                    AppAuthManagerException(
                        AppAuthManagerException.INVALID_REFRESH_TOKEN
                    )
                )
            }

            // Create the refresh token grant request
            val tokenRequest: TokenRequest = getTokenRequestBuilder()
                .setGrantType(GrantTypeValues.REFRESH_TOKEN)
                .setRedirectUri(redirectUri)
                .setRefreshToken(refreshToken)
                .build()

            val authService: AuthorizationService = getAuthorizationService(context)

            // Trigger the request
            try {
                authService.performTokenRequest(tokenRequest) { tokenResponse, exception ->
                    when {
                        // Translate AppAuth errors to the display format
                        exception != null -> {
                            // If we get an invalid_grant error it means the refresh token has expired
                            if (exception.type == AuthorizationException.TYPE_OAUTH_TOKEN_ERROR &&
                                exception.code == AuthorizationException.TokenRequestErrors.INVALID_GRANT.code
                            ) {
                                continuation.resumeWithException(
                                    AppAuthManagerException(
                                        AppAuthManagerException.INVALID_REFRESH_TOKEN,
                                        exception.message
                                    )
                                )

                            } else {
                                continuation.resumeWithException(
                                    AppAuthManagerException(
                                        AppAuthManagerException.TOKEN_REQUEST_FAILED,
                                        exception.message
                                    )
                                )
                            }
                        }

                        // Sanity check
                        tokenResponse == null -> {
                            continuation.resumeWithException(
                                AppAuthManagerException(
                                    AppAuthManagerException.EMPTY_TOKEN_RESPONSE
                                )
                            )
                        }

                        // return the token response
                        else -> {
                            continuation.resume(tokenResponse)
                        }
                    }
                }
            } catch (exception: Exception) {
                continuation.resumeWithException(
                    AppAuthManagerException(
                        AppAuthManagerException.TOKEN_REQUEST_FAILED,
                        exception.message
                    )
                )
            } finally {
                authService.dispose()
            }
        }
    }

    suspend fun performActionWithFreshTokens(
        context: Context,
        action: suspend (String, String) -> Unit
    ): AuthState? = withContext(Dispatchers.IO) {
        suspendCoroutine { continuation ->
            val authState = _appAuthState ?: throw AppAuthManagerException(
                AppAuthManagerException.INVALID_AUTH_STATE
            )

            val authService: AuthorizationService = getAuthorizationService(context)

            if (authState.isAuthorized) {
                authState.performActionWithFreshTokens(authService)
                { accessToken, idToken, exception ->
                    if (exception != null) {
                        continuation.resumeWithException(
                            AppAuthManagerException(
                                AppAuthManagerException.TOKEN_REQUEST_FAILED,
                                exception.message
                            )
                        )
                    } else {
                        _appAuthState = authState

                        CoroutineScope(Dispatchers.IO).launch {
                            action(accessToken!!, idToken!!)
                        }

                        continuation.resume(authState)
                    }
                }
            } else {
                continuation.resumeWithException(
                    AppAuthManagerException(AppAuthManagerException.INVALID_AUTH_STATE)
                )
            }
        }
    }
}
