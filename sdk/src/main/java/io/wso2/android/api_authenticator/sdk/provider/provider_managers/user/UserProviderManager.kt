package io.wso2.android.api_authenticator.sdk.provider.provider_managers.user

import android.content.Context

/**
 * UserProviderManager is responsible for managing the user details.
 */
internal interface UserProviderManager {
    /**
     * Get the user details.
     *
     * @param context The [Context] of the application
     *
     * @return The user details [LinkedHashMap] that contains the user details
     */
    suspend fun getUserDetails(context: Context): LinkedHashMap<String, Any>?
}
