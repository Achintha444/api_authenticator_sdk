package io.wso2.android.api_authenticator.sdk.core.managers.authenticator

import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticatorTypeException

/**
 * Authenticator manager interface.
 * This interface is responsible for handling the authenticator related operations.
 */
interface AuthenticatorManager {
    /**
     * Get full details of the authenticator type.
     *
     * @param flowId Flow id of the authentication flow
     * @param authenticatorType Authenticator type that is required to get the full details
     *
     * @return Authenticator type with full details [AuthenticatorType]
     *
     * @throws AuthenticatorTypeException
     */
    suspend fun getDetailsOfAuthenticatorType(
        flowId: String,
        authenticatorType: AuthenticatorType
    ): AuthenticatorType

    /**
     * Get full details of the all authenticators for the given flow.
     *
     * @param flowId Flow id of the authentication flow
     * @param authenticatorTypes List of authenticator types
     *
     * @return List of authenticator types with full details [ArrayList<AuthenticatorType>]
     *
     * @throws AuthenticatorTypeException
     */
    suspend fun getDetailsOfAllAuthenticatorTypesGivenFlow(
        flowId: String,
        authenticatorTypes: ArrayList<AuthenticatorType>
    ): ArrayList<AuthenticatorType>
}