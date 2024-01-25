package io.wso2.android.api_authenticator.sdk.util

import android.content.Context
import android.net.Uri
import io.wso2.android.api_authenticator.sdk.exceptions.AppAuthManagerException
import io.wso2.android.api_authenticator.sdk.models.http_client.CustomHttpURLConnection
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.TokenRequest
import okhttp3.OkHttpClient
import java.lang.ref.WeakReference
import javax.net.ssl.X509TrustManager
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Use to manage the AppAuth SDK.
 *
 * @property httpBuilderClient The [OkHttpClient] instance.
 * @property clientId The client ID.
 * @property tokenEndpoint The token endpoint.
 * @property authorizeEndpoint The authorize endpoint.
 */
class AppAuthManager private constructor(
    private val httpBuilderClient: OkHttpClient,
    private val clientId: String,
    private val tokenEndpoint: String,
    private val authorizeEndpoint: String
) {
    // Set the authorization service configuration
    private var serviceConfig: AuthorizationServiceConfiguration =
        AuthorizationServiceConfiguration(
            Uri.parse(authorizeEndpoint),  // Authorization endpoint
            Uri.parse(tokenEndpoint) // Token endpoint
        )

    // Set the custom trust client
    private val customTrustClient: OkHttpClient = httpBuilderClient

    companion object {
        /**
         * Instance of the [AppAuthManager] class.
         */
        private var appAuthManagerInstance = WeakReference<AppAuthManager?>(null)

        /**
         * Initialize the [AppAuthManager] class.
         *
         * @property httpBuilderClient The [OkHttpClient] instance.
         * @property clientId The client ID.
         * @property tokenEndpoint The token endpoint.
         * @property authorizeEndpoint The authorize endpoint.
         *
         * @return The [AppAuthManager] instance.
         */
        fun getInstance(
            httpBuilderClient: OkHttpClient,
            clientId: String,
            tokenEndpoint: String,
            authorizeEndpoint: String
        ): AppAuthManager {
            var appAuthManager = appAuthManagerInstance.get()
            if (appAuthManager == null) {
                appAuthManager = AppAuthManager(
                    httpBuilderClient,
                    clientId,
                    tokenEndpoint,
                    authorizeEndpoint
                )
                appAuthManagerInstance = WeakReference(appAuthManager)
            }
            return appAuthManager
        }
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
    suspend fun exchangeAuthorizationCodeForAccessToken(
        authorizationCode: String,
        context: Context
    ): String? = suspendCoroutine { continuation ->
        val tokenRequest: TokenRequest = TokenRequest.Builder(
            serviceConfig,
            clientId
        )
            .setAuthorizationCode(authorizationCode)
            .setClientId(clientId)
            .build()
        val authService = AuthorizationService(
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

        try {
            authService.performTokenRequest(tokenRequest) { response, ex ->
                if (response != null) {
                    // Access token obtained successfully
                    val accessToken: String = response.accessToken!!
                    continuation.resume(accessToken)
                } else {
                    val exception = AppAuthManagerException(
                        AppAuthManagerException.TOKEN_REQUEST_FAILED
                    )
                    continuation.resumeWithException(exception)
                }
            }
        } catch (ex: Exception) {
            val exception = AppAuthManagerException(
                AppAuthManagerException.TOKEN_REQUEST_FAILED,
                ex.message
            )
            continuation.resumeWithException(exception)
        } finally {
            authService.dispose()
        }
    }
}