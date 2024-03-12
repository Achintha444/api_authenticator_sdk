//import android.app.Activity
//import androidx.activity.ComponentActivity
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import com.google.android.gms.auth.api.Auth
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.android.gms.common.api.GoogleApiClient
//import com.google.android.gms.auth.api.signin.GoogleSignInResult
//
//class AuthenticationSDK(private val activity: ComponentActivity) {
//
//    private val signInLauncher = activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val signInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(result.data)
//            if (signInResult.isSuccess) {
//                val account = signInResult.signInAccount
//                // Handle successful sign-in
//            } else {
//                // Handle sign-in failure
//            }
//        }
//    }
//
//    fun initiateGoogleSignIn() {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .build()
//
//        val googleApiClient = GoogleApiClient.Builder(activity)
//            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//            .build()
//
//        googleApiClient.connect()
//
//        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
//        signInLauncher.launch(signInIntent)
//    }
//}
