package io.wso2.android.api_authenticator.sdk.providers.util

import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.AuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.autheniticator_type.BasicAuthenticatorType
import io.wso2.android.api_authenticator.sdk.models.authentication_flow.AuthenticationFlow
import io.wso2.android.api_authenticator.sdk.models.exceptions.AuthenticatorTypeException
import io.wso2.android.api_authenticator.sdk.models.flow_status.FlowStatus
import io.wso2.android.api_authenticator.sdk.providers.authentication.AuthenticationState
import io.wso2.android.api_authenticator.sdk.util.AuthenticatorTypeUtil
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Utility class for the [AuthenticationManager]
 *
 * This class contains utility methods that are used by the [AuthenticationManager]
 */
object AuthenticationManagerUtil {
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

        if (hasDuplicates) {
            return null
        } else {
            return authenticatorType
        }
    }


}