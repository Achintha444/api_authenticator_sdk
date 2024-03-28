package io.wso2.android.api_authenticator.sdk.core.managers.native_authentication_handler.google_native_authentication_handler.impl

import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption

object GoogleNativeAuthenticationHandlerManagerImplRequestBuilder {

    internal fun getAuthenticateWithGoogleNativeRequestBuilder(
        googleIdOptions: GetGoogleIdOption
    ): GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOptions)
        .build()

    internal fun getGoogleLoginRequestBuilder(): ClearCredentialStateRequest =
        ClearCredentialStateRequest()
}
