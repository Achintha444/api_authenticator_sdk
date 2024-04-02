package io.wso2.android.api_authenticator.sdk.provider.util

import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.util.AuthenticatorTypeUtil

/**
 * Utility class for the [AuthenticatorType]
 *
 * This class contains utility methods that are used by the [AuthenticatorType]
 */
object AuthenticatorProviderUtil {
    /**
     * Get the authenticator type from the authenticator type list
     *
     * @param authenticators List of authenticators
     * @param authenticatorTypeString Authenticator type string
     *
     * @return [AuthenticatorType] object, `null` if the authenticator type is not found
     * or if there are duplicates authenticators of the given authenticator type in the given step
     */
    fun getAuthenticatorTypeFromAuthenticatorTypeList(
        authenticators: ArrayList<AuthenticatorType>,
        authenticatorTypeString: String
    ): AuthenticatorType? {
        val authenticatorType: AuthenticatorType =
            AuthenticatorTypeUtil.getAuthenticatorTypeFromAuthenticatorTypeList(
                authenticators,
                authenticatorTypeString
            ) ?: return null

        val hasDuplicates: Boolean = AuthenticatorTypeUtil.hasDuplicatesAuthenticatorsInGivenStep(
            authenticators,
            authenticatorTypeString
        )

        return if (hasDuplicates) null else authenticatorType
    }

    /**
     * Get the authenticator type from the authenticator type list
     *
     * @param authenticators List of authenticators
     * @param authenticatorIdString Authenticator id string
     *
     * @return [AuthenticatorType] object, `null` if the authenticator type is not found
     * or if there are duplicates authenticators of the given authenticator type in the given step
     */
    fun getAuthenticatorTypeFromAuthenticatorTypeListOnAuthenticatorId(
        authenticators: ArrayList<AuthenticatorType>,
        authenticatorIdString: String
    ): AuthenticatorType? {
        val authenticatorType: AuthenticatorType =
            AuthenticatorTypeUtil.getAuthenticatorTypeFromAuthenticatorTypeListOnAuthenticatorId(
                authenticators,
                authenticatorIdString
            ) ?: return null

        val hasDuplicates: Boolean =
            AuthenticatorTypeUtil.hasDuplicatesAuthenticatorsInGivenStepOnAuthenticatorId(
                authenticators,
                authenticatorIdString
            )

        return if (hasDuplicates) null else authenticatorType
    }
}