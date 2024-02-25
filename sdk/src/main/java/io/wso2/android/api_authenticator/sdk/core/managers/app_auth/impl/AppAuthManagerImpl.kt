package io.wso2.android.api_authenticator.sdk.core.managers.app_auth.impl

import android.content.Context
import io.wso2.android.api_authenticator.sdk.core.managers.app_auth.AppAuthManager
import io.wso2.android.api_authenticator.sdk.models.exceptions.AppAuthManagerException
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
 * @property customTrustClient The [OkHttpClient] instance.
 * @property clientId The client ID.
 * @property serviceConfig The [AuthorizationServiceConfiguration] instance.
 */
internal class AppAuthManagerImpl private constructor(
    private val customTrustClient: OkHttpClient,
    private val clientId: String,
    private val serviceConfig: AuthorizationServiceConfiguration
): AppAuthManager {
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
     * Use to exchange the authorization code for the access token.
     *
     * @param authorizationCode The authorization code.
     *
     * @throws AppAuthManagerException If the token request fails.
     *
     * @return The access token [String]
     */
    override suspend fun getAccessToken(
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
