package io.wso2.android.api_authenticator.sdk.models.http_client.http_client_builder

import io.wso2.android.api_authenticator.sdk.models.http_client.LessSecureHttpClient
import io.wso2.android.api_authenticator.sdk.models.http_client.SecureHttpClient
import okhttp3.OkHttpClient
import java.io.InputStream

/**
 * Use to create the [OkHttpClient] for API calls in the SDK.
 */
internal class HttpClientBuilder {
    companion object {
        /**
         * Returns an instance of the [OkHttpClient] class, based on the given parameters.
         *
         * @property trustedCertificates The certificate(in the PEM format) of the WSO2 identity
         * server as a [InputStream] - optional. If not provided, a less secure http client will be
         * used, which bypasses the certificate validation. `This is not recommended for production`.
         */
        internal fun getHttpClientInstance(trustedCertificates: InputStream?): OkHttpClient {
            return if (trustedCertificates != null) {
                SecureHttpClient.getInstance(trustedCertificates).client
            } else {
                LessSecureHttpClient.getInstance().client
            }
        }
    }
}