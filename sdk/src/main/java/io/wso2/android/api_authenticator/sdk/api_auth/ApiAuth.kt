package io.wso2.android.api_authenticator.sdk.api_auth

import io.wso2.android.api_authenticator.sdk.api_auth.di.ApiAuthContainer
import io.wso2.android.api_authenticator.sdk.core.AuthenticationCoreConfig
import io.wso2.android.api_authenticator.sdk.core.core_types.authentication.AuthenticationCoreDef
import io.wso2.android.api_authenticator.sdk.core.core_types.authentication.impl.AuthenticationCore
import io.wso2.android.api_authenticator.sdk.provider.di.AuthenticationProviderImplContainer
import io.wso2.android.api_authenticator.sdk.provider.di.TokenProviderImplContainer
import io.wso2.android.api_authenticator.sdk.provider.providers.authentication.AuthenticationProvider
import io.wso2.android.api_authenticator.sdk.provider.providers.authentication.impl.AuthenticationProviderImpl
import io.wso2.android.api_authenticator.sdk.provider.providers.token.TokenProvider
import io.wso2.android.api_authenticator.sdk.provider.providers.token.impl.TokenProviderImpl
import java.lang.ref.WeakReference

/**
 * The [ApiAuth] class act as the entry point for the SDK.
 * This class will initialize the [AuthenticationProvider] and [TokenProvider] instances,
 * which will be used throughout the application for authentication and token management.
 *
 * @param authenticationCoreConfig Configuration of the Authenticator [AuthenticationCoreConfig]
 */
class ApiAuth private constructor(private val authenticationCoreConfig: AuthenticationCoreConfig) {

    /**
     * Instance of the [AuthenticationCore] that will be used throughout the application
     */
    private val authenticationCore: AuthenticationCoreDef by lazy {
        ApiAuthContainer.getAuthenticationCoreDef(authenticationCoreConfig)
    }

    companion object {
        /**
         * Instance of the [ApiAuth] that will be used throughout the application
         */
        private var apiAuthInstance = WeakReference<ApiAuth?>(null)

        /**
         * Initialize the [ApiAuth] instance and return the instance.
         *
         * @param authenticationCoreConfig Configuration of the Authenticator [AuthenticationCoreConfig]
         *
         * @return Initialized [AuthenticationCore] instance
         */
        fun getInstance(authenticationCoreConfig: AuthenticationCoreConfig): ApiAuth {
            var apiAuth = apiAuthInstance.get()
            if (apiAuth == null) {
                apiAuth = ApiAuth(authenticationCoreConfig)
                apiAuthInstance = WeakReference(apiAuth)
            }
            return apiAuth
        }

        /**
         * Get the [ApiAuth] instance.
         * This method will return null if the [ApiAuth] instance is not initialized.
         *
         * @return [ApiAuth] instance
         */
        fun getInstance(): ApiAuth? = apiAuthInstance.get()
    }

    /**
     * Get the [AuthenticationProvider] instance. This instance will be used for authentication.
     *
     * @return [AuthenticationProvider] instance
     */
    fun getAuthenticationProvider(): AuthenticationProvider =
        AuthenticationProviderImpl.getInstance(
            AuthenticationProviderImplContainer.getAuthenticationProviderManager(authenticationCore)
        )

    /**
     * Get the [TokenProvider] instance. This instance will be used for token management.
     *
     * @return [TokenProvider] instance
     */
    fun getTokenProvider(): TokenProvider = TokenProviderImpl.getInstance(
        TokenProviderImplContainer.getTokenProviderManager(authenticationCore)
    )
}