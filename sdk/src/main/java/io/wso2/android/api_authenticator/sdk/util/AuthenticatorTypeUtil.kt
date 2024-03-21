package io.wso2.android.api_authenticator.sdk.util

import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType

/**
 * Util class to handle the authenticator types
 */
object AuthenticatorTypeUtil {
    /**
     * Check whether there are duplicates authenticators of the given authenticator type in the given step
     *
     * @param authenticators List of authenticators
     * @param authenticatorTypeString Authenticator type string
     *
     * @return Boolean value whether there are duplicates authenticators of the given authenticator type in the given step
     */
    fun hasDuplicatesAuthenticatorsInGivenStep(
        authenticators: ArrayList<AuthenticatorType>,
        authenticatorTypeString: String
    ): Boolean {
        return authenticators.count { it.authenticator == authenticatorTypeString } > 1
    }

    /**
     * Get the authenticator type from the authenticator type list
     *
     * @param authenticators List of authenticators
     * @param authenticatorTypeString Authenticator type string
     *
     * @return [AuthenticatorType] object, `null` if the authenticator type is not found
     */
    fun getAuthenticatorTypeFromAuthenticatorTypeList(
        authenticators: ArrayList<AuthenticatorType>,
        authenticatorTypeString: String
    ): AuthenticatorType? {
        return authenticators.find { it.authenticator == authenticatorTypeString }
    }

    /**
     * Check whether there are duplicates authenticators of the given authenticator type in the given step
     *
     * @param authenticators List of authenticators
     * @param authenticatorIdString Authenticator id string
     *
     * @return Boolean value whether there are duplicates authenticators of the given authenticator type in the given step
     */
    fun hasDuplicatesAuthenticatorsInGivenStepOnAuthenticatorId(
        authenticators: ArrayList<AuthenticatorType>,
        authenticatorIdString: String
    ): Boolean {
        return authenticators.count { it.authenticatorId == authenticatorIdString } > 1
    }

    /**
     * Get the authenticator type from the authenticator type list
     *
     * @param authenticators List of authenticators
     * @param authenticatorIdString Authenticator id string
     *
     * @return [AuthenticatorType] object, `null` if the authenticator type is not found
     */
    fun getAuthenticatorTypeFromAuthenticatorTypeListOnAuthenticatorId(
        authenticators: ArrayList<AuthenticatorType>,
        authenticatorIdString: String
    ): AuthenticatorType? {
        return authenticators.find { it.authenticatorId == authenticatorIdString }
    }
}