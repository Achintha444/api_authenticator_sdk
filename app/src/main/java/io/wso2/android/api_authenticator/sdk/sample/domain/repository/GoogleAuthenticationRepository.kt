package io.wso2.android.api_authenticator.sdk.sample.domain.repository

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

interface GoogleAuthenticationRepository {
    fun signInWithGoogle(context: Context, activityResultLauncher: ActivityResultLauncher<Intent>)

    fun handleGoogleSignInResult(data: Intent?)
}