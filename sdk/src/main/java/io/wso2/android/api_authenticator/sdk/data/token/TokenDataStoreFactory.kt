package io.wso2.android.api_authenticator.sdk.data.token

import android.content.Context
import io.wso2.android.api_authenticator.sdk.data.token.impl.TokenDataStoreImpl

/**
 * Factory class to get the instance of the [TokenDataStore].
 *
 * @property instances The [MutableMap] instance.
 */
internal object TokenDataStoreFactory {
    private val instances = mutableMapOf<Context, TokenDataStore>()

    /**
     * Get the instance of the [TokenDataStore] based on the [Context], if
     * the instance is already created for the given [Context] then it will
     * return the same instance.
     *
     * @param context The [Context] instance.
     *
     * @return The [TokenDataStore] instance.
     */
    fun getTokenDataStore(context: Context): TokenDataStore =
        instances.getOrPut(context) {
            TokenDataStoreImpl(context)
        }
}
