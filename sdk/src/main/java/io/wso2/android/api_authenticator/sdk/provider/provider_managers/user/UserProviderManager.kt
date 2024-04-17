package io.wso2.android.api_authenticator.sdk.provider.provider_managers.user

import android.content.Context

/**
 * UserProviderManager is responsible for managing the user details.
 */
internal interface UserProviderManager {
    /**
     * Get the basic user information of the authenticated.
     *
     * @param context The [Context] of the application
     *
     * @return User details as a [LinkedHashMap]
     */
     suspend fun getBasicUserInfo(context: Context): LinkedHashMap<String, Any>?
}
