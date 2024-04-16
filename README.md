# Developer Documentation - For Android SDK

## Introduction

The Asgardeo Auth Android SDK enables Android applications (written in Kotlin) to utilize OpenID Connect (OIDC) authentication with the WSO2 Identity Server/Asgardeo serving as the Consumer Identity and Access Management (CIAM) Provider through API-based authentication. This SDK assists you in creating custom login flows directly within the applications themselves, without relying on browser redirects, thereby prioritizing user experience.

## Prerequisite

- Supports Android applications written in Kotlin programming language.
- The minimum supported SDK is API level 26, compiled to API level 34. However, there are certain limitations when using specific authentication methods:
  - Passkeys are supported on API level 34 and above.
  - Google authentication using the Credential Manager API is supported on API level 34 and above.

## Getting Started

### Register your application in /WSO2 Identity Server/Asgardeo

#### WSO2 Identity Server

1. Download the latest version of WSO2 Identity Server, and start the WSO2 Identity Server.
2. Register a Mobile Application to integrate your application with WSO2 Identity Server. You will obtain a `client_ID` from WSO2 Identity Server for your application which will need to be embedded later for the SDK integration. Also note the redirect URI that you used to create the application, this is also required for the SDK integration.

#### Asgardeo

1. Register to Asgardeo and create an organization if you don't already have one. The organization name you choose will be referred to as `<org_name>` throughout this document.
2. Register a Mobile Application in Asgardeo to integrate your application with Asgardeo. You will obtain a `client_ID` from Asgardeo for your application which will need to be embedded later for the SDK integration. Also note the redirect URI that you used to create the application, this is also required for the SDK integration.

### Installing the SDK

Add the latest released SDK in the `build.gradle` file of your Android application.

```groovy
dependencies {
    implementation 'io.wso2.android.api_authenticator.sdk:1.0.0'
}
```

Add a redirect scheme in the Android application. You need to add the `appAuthRedirectScheme` in the application `build.gradle` file.

This should be consistent with the CallBack Url of the Service Provider that you configured in the WSO2 Identity Server/Asgardeo.

For example, if you have configured the `callBackUrl` as `wso2sample://oauth2`, then the `appAuthRedirectScheme` should be `wso2sample`.

```groovy
android.defaultConfig.manifestPlaceholders = [
    'appAuthRedirectScheme': 'wso2sample'
]
```

### Start the authentication process

1. First, you need to initialize the SDK object, `AsgardeoAuth`, to authenticate users into your application. This can be done in a repository if you are using an MVVM pattern in your application.

```kotlin
private val asgardeoAuth: AsgardeoAuth = AsgardeoAuth.getInstance(
    AuthenticationCoreConfig(
        "https://localhost:9443",
        "wso2sample://oauth2",
        "<client_id>",
        "openid"
    )
)
```
> [!IMPORTANT]
> If you are using an emulator to try with a locally hosted IS instance, make sure to replace localhost with 10.0.2.2 !

`AuthenticationCoreConfig` holds the configuration details that are required to set up the communication between the SDK and the WSO2 Identity Server/Asgardeo.

2. After that, you need to get the `AuthenticationProvider` from the created `AsgardeoAuth` instance. This will assist you in handling the authentication process.

```kotlin
val authenticationProvider: AuthenticationProvider = asgardeoAuth.getAuthenticationProvider()
```

`AuthenticationProvider` handles the authentication process using `SharedFlow`. This will help you to handle each state of the authentication process easily. There are four states in the authentication process:

- `AuthenticationState.Initial`: Initial state of the authentication process.
- `AuthenticationState.Loading`: SDK is calling an API to handle the authentication and waiting for the result.
- `AuthenticationState.Unauthenticated`: User is not authenticated. In this state, the list of available authenticators will be returned to you in a `AuthenticationFlowNotSuccess` object.
- `AuthenticationState.Authenticated`: User is authenticated.

3. To start the authentication process, call `authenticationProvider.isLoggedInStateFlow`, this will check if there is an active session available and if available, the authentication state will emit `AuthenticationState.Authenticated`, else will emit `AuthenticationState.Initial`.

After that, you can call the `authenticationProvider.initializeAuthentication` to initialize the authentication process.

```kotlin
@Composable
internal fun LandingScreen() {
    val state = authenticationProvider.getAuthenticationStateFlow()
    authenticationProvider.isLoggedInStateFlow(context)
    handleAuthenticationState(state)
}

private fun handleAuthenticationState(state: AuthenticationState) {
    authStateJob = state.collect {
        when (it) {
            is AuthenticationState.Initial -> {
                authenticationProvider.initializeAuthentication(context)
            }
            is AuthenticationState.Unauthorized -> {
                // Display login form
                LoginForm(it.authenticationFlow)
            }
            is AuthenticationState.Error -> {
                // Display Error Toast
            }
            is AuthenticationState.Authorized -> {
                onSuccessfulLogin()
            }
            is AuthenticationState.Loading -> {
                // Show loading
            }
        }
    }
}
```

```kotlin
/**
 * Assuming the authentication process is MFA with first factor is basic
 * authenticator and second factor is TOTP
 */
@Composable
internal fun LoginForm() {
    authenticationFlow: AuthenticationFlowNotSuccess,
    onSuccessfulLogin: (User) -> Unit
) {
    authenticationFlow.nextStep.authenticators.forEach {
        when (it.authenticator) {
            AuthenticatorTypes.BASIC_AUTHENTICATOR.authenticatorType -> {
                BasicAuth(authenticatorType = it)
            }

            AuthenticatorTypes.TOTP_AUTHENTICATOR.authenticatorType -> {
                TotpAuth(authenticatorType = it)
            }
        }
    }
}

@Composable
internal fun BasicAuth(authenticatorType: AuthenticatorType) {
    BasicAuthComponent(
        onLoginClick = { username, password ->
            authenticationProvider.authenticateWithUsernameAndPassword(
                username = username,
                password = password
            )
        }
    )
}

@Composable
fun BasicAuthComponent(
    onLoginClick: (username: String, password: String) -> Unit
) {
    Column() {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username"
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password"
        )
        Button(onClick = { onLoginClick(username, password) }) {
            Text(text = "Login")
        }
    }
}
```

You will not need to handle the authentication state in multiple places, you can do it at the start of the application, and it will handle the state accordingly.

### Get user details

After the user is authenticated, to get user-related information, we can use the following function. This will return the user details in a `LinkedHashMap`.

```kotlin
coroutineScope.launch {
    runCatching {
        authenticationProvider.getUserDetails(<context>)
    }.onSuccess { userDetails ->
        Profile(userDetails)
    }.onFailure { e ->
        // Display error message
    }
}
```

### Get token information

To get information, you can use the `TokenProvider`. This will assist you in getting token-related information and performing actions on the tokens.

```kotlin
val tokenProvider: TokenProvider = asgardeoAuth.getTokenProvider()
```

To get the token-related information, you can use the following functions:

```kotlin
val accessToken: String? = tokenProvider.getAccessToken(context)
val idToken: String? = tokenProvider.getIDToken(context)
val refreshToken: String? = tokenProvider.getRefreshToken(context)
val accessTokenExpirationTime: Long? = tokenProvider.getAccessTokenExpirationTime(context)
val scope:String? = tokenProvider.getScope(context)
```

### Perform action based on the tokens

If you want to perform any action based on the tokens that are returned, you can use the `performAction` function in the `TokenProvider`.

```kotlin
tokenProvider.performAction(context) { accessToken, idToken, ->
    action(accessToken, idToken)
}
```

### Logout

If you want to perform a logout, you can call the `logout` function in the `AuthenticationProvider`. This will emit the state `AuthenticationState.Initial` if the logout is successful, and if an error occurs, it will emit `AuthenticationState.Error`.

```kotlin
authenticationProvider.logout(context)
```

## Use authenticators with the SDK

The Asgardeo Auth SDK provides out-of-the-box support for some authenticators, which are accessible via the `AuthenticationProvider`. Each of the following functions will emit the aforementioned `AuthenticationStates`, except for the `AuthenticationState.Initial`.

Before utilizing these authenticators, you need to integrate them into your application's login flow. You can find more information about this in the following link: [link_to_documentation].

### Use Basic authentication

```kotlin
authenticationProvider.authenticateWithUsernameAndPassword(
    context = context,
    authenticatorId = authenticatorType.authenticatorId,
    username = username,
    password = password
)
```

### Use TOTP authentication

```kotlin
authenticationProvider.authenticateWithTotp(
    context = context,
    authenticatorId = authenticatorType.authenticatorId,
    token = token
)
```

### Use Google authentication

#### Using credential manager API (Supports API 34 and above)

```kotlin
authenticationProvider.authenticateWithGoogle(
    context,    
    authenticatorId = authenticatorType.authenticatorId
)
```

#### Using legacy one tap (Not recommended for newer applications)

```kotlin
val launcher: ActivityResultLauncher<Intent> = rememberLauncherForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    authenticationProvider.handleGoogleNativeLegacyAuthenticateResult(
        context,
        result.resultCode,
        result.data
    )
}

authenticationProvider.authenticateWithGoogleLegacy(
    context,
    authenticatorId = authenticatorType.authenticatorId
    launcher
)
```

If you are not using Jetpack Compose for development, you can call `handleGoogleNativeLegacyAuthenticateResult` in the activity `onActivityResult`.

```kotlin
class YourActivity : AppCompatActivity() {
    // ...

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        authenticationProvider.handleGoogleNativeLegacyAuthenticateResult(
            context,
            result.resultCode,
            result.data
        )
    }
    // ...
}
```

This will invoke the Google one tap authentication mechanism.

### Use Passkey authentication (Supports API 34 and above)

```kotlin
authenticationProvider.authenticateWithPasskey(
    context,
    authenticatorId = authenticatorType.authenticatorId,
)
```

### Use federated authentication

To perform federated authentication, first, you need to add the following code snippet to your application's `AndroidManifest` file. This will add a separate activity that will handle the redirection on your behalf.

```xml
<application ...>
    ...
    <activity
        android:name="io.wso2.android.api_authenticator.sdk.core.ui.RedirectUriReceiverActivity"
        android:exported="true"
    >
        <intent-filter>
            <category android:name="android.intent.category.BROWSABLE" />
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <data
                android:host="<host>"
                android:scheme="<scheme>" 
            />
        </intent-filter>
    </activity>
</application>
```

After authenticating with the federated IdP, normally, the IdP will redirect the user to the WSO2 Identity Server/Asgardeo's common auth endpoint to continue the flow. However, with API-based authentication, this is changed. The IdP should redirect to the application. To support this, you should configure the deep link in the federated IdP side. Add that deep link in the `<data>` section. For example, if you are using the `wso2sample://oauth2` deep link, you should fill the `<data>` section as follows:

```xml
<data
    android:host="oauth2"
    android:scheme="wso2sample" 
/>
```

### Use federated Github authentication

```kotlin
authenticationProvider.authenticateWithGithubRedirect(
    context,
    authenticatorId = authenticatorType.authenticatorId
)
```

### Use federated Microsoft authentication

```kotlin
authenticationProvider.authenticateWithMicrosoftRedirect(
    context,
    authenticatorId = authenticatorType.authenticatorId
)
```

### Use OpenId Connect authentication

```kotlin
authenticationProvider.authenticateWithOpenIdConnect(
   context,
    authenticatorId = authenticatorType.authenticatorId
)
```

### Use other federated social authentication

To perform authentication with other federated social IdPs, you can use the `authenticateWithOpenIdConnect` function. For this, you need to pass the authenticator id or authenticator type which can be retrieved from the `authenticationFlow` returned from the `Authentication.Unauthenticated` state.

```kotlin
@Composable
internal fun LoginForm() {
    authenticationFlow: AuthenticationFlowNotSuccess,
    onSuccessfulLogin: (User) -> Unit
) {
    authenticationFlow.nextStep.authenticators.forEach {
        when (it.authenticator) {
            "Federated" -> {
                FederatedAuth(authenticatorType = it)
            }
        }
    }
}

@Composable
internal fun FederatedAuth(authenticatorType: AuthenticatorType) {
    FederatedAuthComponent(
        onLoginClick = { username, password ->
            authenticationProvider.authenticateWithOpenIdConnect(
                context = context,
                authenticatorId = authenticatorType.authenticatorId
            )
        }
    )
}
```

### Use any other authentication mechanism

If you are using any other authentication mechanism like email OTP, you can use the `authenticateWithAnyAuthenticator` function. For this, you need to pass the authenticator id or authenticator type which can be retrieved from the `authenticationFlow` returned from the `Authentication.Unauthenticated` state.

This can be used in two ways:

#### Authenticator parameters are known

If you are aware of the authenticator parameters required for the authenticator (which can be found in the following link), you can directly call this function to authenticate the user with this authenticator.

```kotlin
authenticationProvider.authenticateWithAnyAuthenticator(
    context,
    authenticatorId = authenticatorType.authenticatorId,
    authenticatorTypeString = authenticatorType.authenticator,
    authParams = < as a LinkedHashMap<String, String> >
)
```

#### Authenticator parameters are not known

If you are not aware of the authenticator parameters required for the authenticator, you first need to retrieve the parameters required to authenticate the user with this authenticator. For this, you can use the following function:

```kotlin
val detailedAuthenticatorType: AuthenticatorType = authenticationProvider.authenticateWithAuthenticator(
    authenticatorId = authenticatorType.authenticatorId,
    authenticatorTypeString = authenticatorType.authenticator
)
```

This will return a fully detailed authenticator type object. In that object, you can get the required authentication parameters from:

```kotlin
val requiredParams: List<String>? = detailedAuthenticatorType.requiredParams
```

After that, you can manually set the relevant required authentication parameters and call the `authenticateWithAnyAuthenticator` function:

```kotlin
authenticationProvider.authenticateWithAnyAuthenticator(
    context,
    authenticatorId = authenticatorType.authenticatorId,
    authenticatorTypeString = authenticatorType.authenticator,
    authParams = < as a LinkedHashMap<String, String> >
)
```
