package io.wso2.android.api_authenticator.sdk.core.managers.user

/**
 * UserManager is responsible for managing the user details.
 */
interface UserManager {
    /**
     * get the user details from the Identity Server.
     *
     * @param accessToken Access token to authorize the request
     *
     * @return User details as a [LinkedHashMap]
     */
    suspend fun getUserDetails(accessToken: String?): LinkedHashMap<String, Any>?
}
