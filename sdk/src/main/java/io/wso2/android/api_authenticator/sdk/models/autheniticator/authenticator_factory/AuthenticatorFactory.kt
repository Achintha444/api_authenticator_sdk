package io.wso2.android.api_authenticator.sdk.models.autheniticator.authenticator_factory

import io.wso2.android.api_authenticator.sdk.models.autheniticator.Authenticator
import io.wso2.android.api_authenticator.sdk.models.autheniticator.meta_data.AuthenticatorMetaData

/**
 * Authenticator factory
 */
internal object AuthenticatorFactory {
    /**
     * Get authenticator from authenticator id, authenticator name, identity provider, metadata and required parameters
     *
     * @param authenticatorId Authenticator id
     * @param authenticator Authenticator name
     * @param idp Identity provider
     * @param metadata Metadata of the authenticator
     * @param requiredParams Required parameters of the authenticator
     *
     * @return Authenticator [Authenticator]
     */
    internal fun getAuthenticator(
        authenticatorId: String,
        authenticator: String?,
        idp: String?,
        metadata: AuthenticatorMetaData?,
        requiredParams: List<String>?
    ): Authenticator = Authenticator(authenticatorId, authenticator, idp, metadata, requiredParams)
}
