package io.wso2.android.api_authenticator.sdk.providers.authentication

import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow

/**
 * Authentication state of the application
 * This sealed class is used to represent the different states of the authentication process
 *
 * States:
 * - [Unauthorized]: The user is not authorized to access the application
 * - [Authorized]: The user is authorized to access the application
 * - [Loading]: The application is in the process of loading the authentication state
 * - [Error]: An error occurred during the authentication process
 */
sealed class AuthenticationState {
    data class Unauthorized(val authenticationFlow: AuthenticationFlow?) : AuthenticationState()
    object Authorized : AuthenticationState()
    object Loading : AuthenticationState()
    data class Error(val throwable: Throwable) : AuthenticationState()
}
