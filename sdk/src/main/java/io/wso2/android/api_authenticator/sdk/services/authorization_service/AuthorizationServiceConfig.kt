package io.wso2.android.api_authenticator.sdk.services.authorization_service

import java.io.InputStream

/**
 * Holds the configuration related to the `AuthorizationService`.
 *
 * @property authorizeUri Authorization endpoint
 * @property authnUri Authentication next step endpoint
 * @property clientId Client id of the application
 * @property scope Scope of the application (ex: openid profile email)
 * @property trustedCertificates Trusted certificates of the WSO2 identity server(in the PEM format)
 * as a [InputStream] - optional. If not provided, a less secure http client will be used, which
 * bypasses the certificate validation. `This is not recommended for production`.
 */
class AuthorizationServiceConfig (
    val authorizeUri: String,
    val authnUri: String,
    val clientId: String,
    val scope: String,
    val trustedCertificates: InputStream? = null
)
