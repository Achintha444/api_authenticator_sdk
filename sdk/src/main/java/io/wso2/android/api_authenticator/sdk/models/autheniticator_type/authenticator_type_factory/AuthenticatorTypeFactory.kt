package io.wso2.android.api_authenticator.sdk.models.autheniticator_type.authenticator_type_factory

import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.meta_data.AuthenticatorTypeMetaData

/**
 * Authenticator type factory
 */
internal object AuthenticatorTypeFactory {
    /**
     * Get authenticator type from authenticator id, authenticator name, identity provider, metadata and required parameters
     *
     * @param authenticatorId Authenticator id
     * @param authenticator Authenticator name
     * @param idp Identity provider
     * @param metadata Metadata of the authenticator type
     * @param requiredParams Required parameters of the authenticator type
     *
     * @return Authenticator type `AuthenticatorType`
     */
    internal fun getAuthenticatorType(
        authenticatorId: String,
        authenticator: String?,
        idp: String?,
        metadata: AuthenticatorTypeMetaData?,
        requiredParams: List<String>?
    ): AuthenticatorType {
        /*return when (authenticator) {
            BasicAuthenticatorType.AUTHENTICATOR_TYPE -> {
                val authenticatorTypeMetaData = BasicAuthenticatorTypeMetaData(
                    metadata?.promptType,
                    metadata?.params
                )
                BasicAuthenticatorType(
                    authenticatorId,
                    authenticator,
                    idp!!,
                    authenticatorTypeMetaData,
                    requiredParams
                )
            }

            GoogleAuthenticatorType.AUTHENTICATOR_TYPE -> {
                val authenticatorTypeMetaData = GoogleAuthenticatorTypeMetaData(
                    metadata?.i18nKey,
                    metadata?.promptType,
                    GoogleAuthenticatorTypeMetaData.GoogleAdditionalData(
                        metadata?.additionalData?.nonce!!,
                        metadata.additionalData?.clientId!!,
                        metadata.additionalData?.scope!!
                    )
                )
                GoogleAuthenticatorType(
                    authenticatorId,
                    authenticator,
                    idp!!,
                    authenticatorTypeMetaData,
                    requiredParams
                )
            }

            PasskeyAuthenticatorType.AUTHENTICATOR_TYPE -> {
                val authenticatorTypeMetaData = PasskeyAuthenticatorTypeMetaData(
                    metadata?.i18nKey,
                    metadata?.promptType,
                    PasskeyAuthenticatorTypeMetaData.PasskeyAdditionalData(
                        challengeData = metadata?.additionalData?.challengeData!!
                    )
                )
                PasskeyAuthenticatorType(
                    authenticatorId,
                    authenticator,
                    idp!!,
                    authenticatorTypeMetaData,
                    requiredParams
                )
            }

            TotpAuthenticatorType.AUTHENTICATOR_TYPE -> {
                val authenticatorTypeMetaData = TotpAuthenticatorTypeMetaData(
                    metadata?.i18nKey!!,
                    metadata.promptType,
                    metadata.params
                )
                TotpAuthenticatorType(
                    authenticatorId,
                    authenticator,
                    idp!!,
                    authenticatorTypeMetaData,
                    requiredParams
                )
            }

            else -> {
                AuthenticatorType(authenticatorId, authenticator, idp, metadata, requiredParams)
            }
        }*/
        return AuthenticatorType(authenticatorId, authenticator, idp, metadata, requiredParams)
    }
}
