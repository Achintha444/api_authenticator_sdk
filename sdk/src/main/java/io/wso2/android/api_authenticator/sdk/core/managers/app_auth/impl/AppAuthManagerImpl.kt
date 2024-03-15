package io.wso2.android.api_authenticator.sdk.core.managers.app_auth.impl

import android.content.Context
import io.wso2.android.api_authenticator.sdk.core.managers.app_auth.AppAuthManager
import io.wso2.android.api_authenticator.sdk.models.exceptions.AppAuthManagerException
import io.wso2.android.api_authenticator.sdk.models.http_client.CustomHttpURLConnection
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationService.TokenResponseCallback
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
 * @property serviceConfig The [AuthorizationServiceConfiguration] instance.
 */
internal class AppAuthManagerImpl private constructor(
    private val customTrustClient: OkHttpClient,
    private val clientId: String,
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
         *
         *
         * @return The [AppAuthManagerImpl] instance.
         */
        fun getInstance(
            httpBuilderClient: OkHttpClient,
            clientId: String,
            serviceConfig: AuthorizationServiceConfiguration
        ): AppAuthManagerImpl {
            var appAuthManagerImpl = appAuthManagerImplInstance.get()
            if (appAuthManagerImpl == null) {
                appAuthManagerImpl = AppAuthManagerImpl(
                    httpBuilderClient,
                    clientId,
                    serviceConfig
                )
                appAuthManagerImplInstance = WeakReference(appAuthManagerImpl)
            }
            return appAuthManagerImpl
        }
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
     * @return The access token [String]
     */
    override suspend fun exchangeAuthorizationCode(
        authorizationCode: String,
        context: Context
    ): TokenResponse? = suspendCoroutine { continuation ->
        val tokenRequest: TokenRequest = getTokenRequestBuilder()
            .setAuthorizationCode(authorizationCode)
            .setClientId(clientId)
            .build()

        val authService: AuthorizationService = getAuthorizationService(context)

        // Define a callback to handle the result of the authorization code grant
        val callback = TokenResponseCallback { tokenResponse, exception ->
            when {
                // Translate AppAuth errors to the display format
                exception != null -> {
                    continuation.resumeWithException(
                        AppAuthManagerException(
                            AppAuthManagerException.TOKEN_REQUEST_FAILED,
                            exception.message
                        )
                    )
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

        // Trigger the request
        try {
            authService.performTokenRequest(tokenRequest, callback)
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
    ): TokenResponse? = suspendCoroutine { continuation ->

        // Check we have a refresh token
        if (refreshToken.isNullOrBlank()) {
            continuation.resumeWithException(
                AppAuthManagerException(
                    AppAuthManagerException.INVALID_REFRESH_TOKEN
                )
            )
        }

        val authService = getAuthorizationService(context)

        // Define a callback to handle the result of the refresh token grant
        val callback = TokenResponseCallback { tokenResponse, exception ->
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

        // Create the refresh token grant request
        val tokenRequest = getTokenRequestBuilder()
            .setGrantType(GrantTypeValues.REFRESH_TOKEN)
            .setRefreshToken(refreshToken)
            .build()

        // Trigger the request
        try {
            authService.performTokenRequest(tokenRequest, callback)
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
