package io.wso2.android.api_authenticator.sdk.core.managers.authenticator

import io.wso2.android.api_authenticator.sdk.models.autheniticator.Authenticator
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticatorException

/**
 * Authenticator manager interface.
 * This interface is responsible for handling the authenticator related operations.
 */
internal interface AuthenticatorManager {
    /**
     * Get full details of the authenticator.
     *
     * @param flowId Flow id of the authentication flow
     * @param authenticator Authenticator object of the selected authenticator
     *
     * @return Authenticator type with full details [Authenticator]
     *
     * @throws AuthenticatorException
     */
    suspend fun getDetailsOfAuthenticator(
        flowId: String,
        authenticator: Authenticator
    ): Authenticator

    /**
     * Remove the instance of the [AuthenticatorManager]
     */
    fun dispose()
}