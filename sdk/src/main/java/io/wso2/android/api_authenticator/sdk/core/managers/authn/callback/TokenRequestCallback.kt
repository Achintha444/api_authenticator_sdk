package io.wso2.android.api_authenticator.sdk.core.managers.authn.callback

import net.openid.appauth.TokenResponse

/**
 * Callback to be used when requesting an access token.
 *
 * @param onSuccess Callback to be called when the token request is finished successfully.
 * @param onFailure Callback to be called when the token request has failed.
 */
class TokenRequestCallback(
    private val onSuccess: (tokenResponse: TokenResponse?) -> Unit,
    private val onFailure: (error: Exception) -> Unit
) {

    /**
     * Called when the token request is finished successfully.
     *
     * @param tokenResponse [TokenResponse] object.
     */
     fun onSuccess(tokenResponse: TokenResponse?) {
        onSuccess.invoke(tokenResponse)
    }

    /**
     * Called when the token request has failed.
     *
     * @param error The error that caused the failure.
     */
    fun onFailure(error: Exception?) {
        onFailure.invoke(error!!)
    }
}