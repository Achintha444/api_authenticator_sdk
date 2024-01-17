package io.wso2.android.api_authenticator.sdk.services.authorization_service

/**
 * Holds the configuration related to the `AuthorizationService`.
 *
 * @property authorizeUri Authorization endpoint
 * @property authnUri Authentication next step endpoint
 * @property clientId Client id of the application
 * @property scope Scope of the application (ex: openid profile email)
 */
class AuthorizationServiceConfig (
    val authorizeUri: String,
    val authnUri: String,
    val clientId: String,
    val scope: String
)
