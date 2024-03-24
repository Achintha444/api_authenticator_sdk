package io.wso2.android.api_authenticator.sdk.util

import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.provider.util.AuthenticatorProviderUtil

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
     fun getAuthenticatorTypeFromAuthenticatorTypeList(
        authenticators: ArrayList<AuthenticatorType>,
        authenticatorIdString: String? = null,
        authenticatorTypeString: String? = null
    ): AuthenticatorType? {
        // setting up the authenticator type
        var authenticatorType: AuthenticatorType? = null

        if (authenticatorIdString != null) {
            authenticatorType =
                AuthenticatorProviderUtil
                    .getAuthenticatorTypeFromAuthenticatorTypeListOnAuthenticatorId(
                        authenticators,
                        authenticatorIdString
                    )
        } else if (authenticatorTypeString != null) {
            authenticatorType =
                AuthenticatorProviderUtil.getAuthenticatorTypeFromAuthenticatorTypeList(
                    authenticators,
                    authenticatorTypeString
                )
        }

        return authenticatorType
    }
}