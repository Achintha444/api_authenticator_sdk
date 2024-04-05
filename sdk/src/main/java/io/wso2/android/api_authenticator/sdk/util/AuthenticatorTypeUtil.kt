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
    internal fun hasDuplicatesAuthenticatorsInGivenStep(
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
    internal fun getAuthenticatorTypeFromAuthenticatorTypeList(
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
    private fun hasDuplicatesAuthenticatorsInGivenStepOnAuthenticatorId(
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
     * or if there are duplicates authenticators of the given authenticator type in the given step
     */
    private fun getAuthenticatorTypeFromAuthenticatorTypeListOnAuthenticatorId(
        authenticators: ArrayList<AuthenticatorType>,
        authenticatorIdString: String,
        authenticatorTypeString: String
    ): AuthenticatorType? {
        val authenticatorType: AuthenticatorType? =
            authenticators.find { it.authenticatorId == authenticatorIdString }

        val hasDuplicates: Boolean = hasDuplicatesAuthenticatorsInGivenStepOnAuthenticatorId(
            authenticators,
            authenticatorIdString
        )

        return if (hasDuplicates) null else {
            if (authenticatorType?.authenticator == authenticatorTypeString) authenticatorType
            else null
        }
    }


    /**
     * Get the authenticator type from the authenticator type list.
     * Done by checking the authenticator id or authenticator type.
     *
     * Precedence: authenticatorId > authenticatorType
     *
     * @param authenticators List of authenticators
     * @param authenticatorIdString The authenticator id string
     * @param authenticatorTypeString The authenticator type string
     */
    internal fun getAuthenticatorTypeFromAuthenticatorTypeList(
        authenticators: ArrayList<AuthenticatorType>,
        authenticatorIdString: String,
        authenticatorTypeString: String
    ): AuthenticatorType? = getAuthenticatorTypeFromAuthenticatorTypeListOnAuthenticatorId(
        authenticators,
        authenticatorIdString,
        authenticatorTypeString
    )
}
