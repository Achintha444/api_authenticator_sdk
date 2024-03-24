package io.wso2.android.api_authenticator.sdk.sample.data.impl.repository

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import io.wso2.android.api_authenticator.sdk.sample.domain.repository.GoogleAuthenticationRepository
import io.wso2.android.api_authenticator.sdk.sample.util.Config
import javax.inject.Inject

class GoogleAuthenticationRepositoryImpl @Inject constructor() : GoogleAuthenticationRepository {
    private val googleSignInOptions: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(Config.getGoogleWebClientId())
            .requestIdToken(Config.getGoogleWebClientId())
            .requestEmail()
            .build()
    }

    override fun signInWithGoogle(context: Context, activityResultLauncher: ActivityResultLauncher<Intent>) {
        val signInIntent = GoogleSignIn.getClient(context, googleSignInOptions).signInIntent
        activityResultLauncher.launch(signInIntent)
    }

    override fun handleGoogleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            // Handle successful sign-in
        } catch (e: ApiException) {
            // Handle sign-in error
        }
    }
}

