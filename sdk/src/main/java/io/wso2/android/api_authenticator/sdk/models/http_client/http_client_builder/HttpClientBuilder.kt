package io.wso2.android.api_authenticator.sdk.models.http_client.http_client_builder

import io.wso2.android.api_authenticator.sdk.models.http_client.LessSecureHttpClient
import io.wso2.android.api_authenticator.sdk.models.http_client.SecureHttpClient
import okhttp3.OkHttpClient
import java.io.InputStream

/**
 * Use to create the [OkHttpClient] for API calls in the SDK.
 */
object HttpClientBuilder {
    /**
     * Returns an instance of the [OkHttpClient] class, based on the given parameters.
     *
     * @property isDevelopment The flag to check whether the app is in development mode or not.
     * If true, the [LessSecureHttpClient] instance will be returned. Otherwise, the default
     * [OkHttpClient] instance will be returned. Default value is true. It is not recommended to
     * keep this value as `true` in production environment.
     *
     * @return [OkHttpClient] instance.
     */
    internal fun getHttpClientInstance(isDevelopment: Boolean?): OkHttpClient {
        return if (isDevelopment == true) {
            LessSecureHttpClient.getInstance().getClient()
        } else {
            OkHttpClient()
        }
    }
}
