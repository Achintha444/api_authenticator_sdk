package io.wso2.android.api_authenticator.sdk.core.managers.authn

import io.wso2.android.api_authenticator.sdk.models.auth_params.AuthParams
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthnManagerException
import java.io.IOException

/**
 * Interface which has the methods to initiate the authorization and authentication flows.
 */
internal interface AuthnManager {

    /**
     * Authorize the application.
     * This method will call the authorization endpoint and get the authenticators available for the
     * first step in the authentication flow.
     */
    suspend fun authorize(): AuthenticationFlow?

    /**
     * Send the authentication parameters to the authentication endpoint and get the next step of the
     * authentication flow. If the authentication flow has only one step, this method will return
     * the success response of the authentication flow if the authentication is successful.
     *
     * @param authenticatorType Authenticator type of the selected authenticator
     * @param authenticatorParameters Authenticator parameters of the selected authenticator
     * as a LinkedHashMap<String, String> with the key as the parameter name and the value as the
     * parameter value
     *
     * @return [AuthenticationFlow] with the next step of the authentication flow
     */
    suspend fun authenticate(
        authenticatorType: AuthenticatorType,
        authenticatorParameters: LinkedHashMap<String, String>,
    ): AuthenticationFlow?

    /**
     * Logout the user from the application.
     *
     * @param clientId Client id of the application
     * @param idToken Id token of the user
     *
     * @throws [AuthnManagerException] If the logout fails
     * @throws [IOException] If the request fails due to a network error
     */
    suspend fun logout(clientId: String, idToken: String): Unit?
}
