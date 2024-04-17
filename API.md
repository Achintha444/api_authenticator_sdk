# Table of Contents

- [AuthenticationCoreConfig](#authenticationcoreconfig)
- [AuthenticationProvider](#authenticationprovider)
  - [Methods](#methods)
    - [fun getAuthenticationStateFlow(): SharedFlow<AuthenticationState>](#fun-getauthenticationstateflow-sharedflowauthenticationstate)
    - [suspend fun isLoggedIn(context: Context): Boolean](#suspend-fun-isloggedincontext-context-boolean)
    - [suspend fun isLoggedInStateFlow(context: Context)](#suspend-fun-isloggedinstateflowcontext-context)
    - [suspend fun initializeAuthentication(context: Context)`](#suspend-fun-initializeauthenticationcontext-context)
    - [suspend fun authenticateWithUsernameAndPassword(context: Context, authenticatorId: String, username: String, password: String)](#suspend-fun-authenticatewithusernameandpasswordcontext-context-authenticatorid-string-username-string-password-string)
    - [suspend fun authenticateWithTotp(context: Context, authenticatorId: String, token: String)](#suspend-fun-authenticatewithtotpcontext-context-authenticatorid-string-token-string)
    - [suspend fun authenticateWithOpenIdConnect(context: Context, authenticatorId: String)](#suspend-fun-authenticatewithopenidhttpcontext-context-authenticatorid-string)
    - [suspend fun authenticateWithMicrosoftRedirect(context: Context, authenticatorId: String)](#suspend-fun-authenticatewithmicrosoftredirectcontext-context-authenticatorid-string)
    - [suspend fun authenticateWithGoogle(context: Context, authenticatorId: String)](#suspend-fun-authenticatewithgooglecontext-context-authenticatorid-string)
    - [suspend fun authenticateWithGoogleLegacy(context: Context, authenticatorId: String, googleAuthenticateResultLauncher: ActivityResultLauncher<Intent>)](#suspend-fun-authenticatewithgooglelegacycontext-context-authenticatorid-string-googleauthenticateresultlauncher-activityresultlauncherintent)
    - [suspend fun handleGoogleNativeLegacyAuthenticateResult(context: Context, resultCode: Int, data: Intent)](#suspend-fun-handlegooglenativelegacyauthenticateresultcontext-context-resultcode-int-data-intent)
    - [suspend fun authenticateWithGithubRedirect(context: Context, authenticatorId: String)](#suspend-fun-authenticatewithgithubredirectcontext-context-authenticatorid-string)
    - [suspend fun authenticateWithPasskey(context: Context, authenticatorId: String, allowCredentials: List<String>? = null, timeout: Long? = null, userVerification: String? = null)](#suspend-fun-authenticatewithpasskeycontext-context-authenticatorid-string-allowcredentials-liststring--null-timeout-long--null-userverification-string--null)
    - [suspend fun authenticate(context: Context, authenticatorId: String, authenticatorTypeString: String, authParams: LinkedHashMap<String, String>)](#suspend-fun-authenticatewithanyauthenticatorcontext-context-authenticatorid-string-authenticatortypestring-string-authparams-linkedhashmapstring-string)
    - [suspend fun getUserDetails(context: Context): LinkedHashMap<String, Any>?](#suspend-fun-getuserdetailscontext-context-linkedhashmapstring-any)
    - [suspend fun logout(context: Context)](#suspend-fun-logoutcontext-context)
- [TokenProvider](#tokenprovider)
  - [Methods](#methods-1)
    - [suspend fun getAccessToken(context: Context): String?](#suspend-fun-getaccesstokencontext-context-string)
    - [suspend fun getRefreshToken(context: Context): String?](#suspend-fun-getrefreshtokencontext-context-string)
    - [suspend fun getIDToken(context: Context): String?](#suspend-fun-getidtokencontext-context-string)
    - [suspend fun getAccessTokenExpirationTime(context: Context): Long?](#suspend-fun-getaccesstokenexpirationtimecontext-context-long)
    - [suspend fun getScope(context: Context): String?](#suspend-fun-getscopecontext-context-string)
    - [suspend fun validateAccessToken(context: Context): Boolean?](#suspend-fun-validateaccesstokencontext-context-boolean)
    - [suspend fun performRefreshTokenGrant(context: Context)](#suspend-fun-performrefreshtokengrantcontext-context)
    - [suspend fun performAction(context: Context, action: suspend (String?, String?) -> Unit)](#suspend-fun-performActioncontext-context-action-suspend-string-string---unit)
    - [suspend fun clearTokens(context: Context): Unit?](#suspend-fun-cleartokenscontext-context-unit)

# AuthenticationCoreConfig

`AuthenticationCoreConfig` holds the configuration details that are required to set up the communication between the SDK and the WSO2 Identity Server/Asgardeo. `AuthenticationCoreConfig` takes the following properties.

| **Property** | **Type** | **Required/Optional** | **Default Value** | **Description** |
| --- | --- | --- | --- | --- |
| `baseUrl` | `String` | Required | `""` | Base URL of the Asgardeo/WSO2 Identity Server application you created. <br><br> Ex: `https://localhost:9443` <br><br> <b>Note: If you are using an emulator to try with a locally hosted IS instance, make sure to replace `localhost` with `10.0.2.2`!</b> |
| `redirectUri` | `String` | Required | `""` | Redirect URI used to create the application in the Asgardeo/WSO2 Identity Server. |
| `clientId` | `String` | Required | `""` | Client ID of the created mobile application in the Asgardeo/WSO2 Identity Server. |
| `scope` | `String` | Required | `""` | Requested scopes. <br><br> Ex: `openid email profile` |
| `integrityToken` | `String` | Optional | `null` | - |
| `googleWebClientId` | `String` | Optional | `null` | Enter the client ID of the Google credential that will be used to create the Google connection in the Asgardeo/WSO2 Identity Server. <br><br> Since we are using the Asgardeo/WSO2 Identity Server to authenticate the user, we need to identify the currently signed-in user on the server. <br><br> To do so securely, after a user successfully signs in, we need to send the user's ID token to the IS using HTTPS. Then, on the server, we are verifying the integrity of the ID token and use the user information contained in the token to establish the session. To generate the user's ID token for the IS, we will require the client ID that is used to create the Google connection in the Asgardeo/WSO2 Identity Server. For more details, refer to [https://developers.google.com/identity/sign-in/android/backend-auth](https://developers.google.com/identity/sign-in/android/backend-auth). |
| `isDevelopment` | `Boolean` | Optional | `false` | Check if the application is in the development stage. If `true`, a less secure HTTP client instance will be used for API calls. <br><br> <b>It is not recommended to keep this value as `false` in the production environment.</b> |

# AuthenticationProvider

The `AuthenticationProvider` is used to handle the authentication process in an application. It provides various methods to perform different authentication operations.

```
val authenticationProvider:AuthenticationProvider = asgardeoAuth.getAuthenticationProvider()
```
AuthenticationProvider handles the authentication process using SharedFlow. This will help the developer to handle each state of the authentication process easily. There are four states in the authentication process:

1. AuthenticationState.Initial: 
    - Initial state of the authentication process. The user is not authenticated to access the application, and need to restart the authentication process.

2. AuthenticationState.Loading: 
    - SDK is calling an API to handle the authentication and waiting for the result.

3. AuthenticationState.Unauthenticated: 
    - User is not authenticated. In this state, the list of available authenticators will be returned to the developer.

4. AuthenticationState.Authenticated: 
    - User is authenticated.


## Methods

### fun getAuthenticationStateFlow(): SharedFlow<AuthenticationState>

- **Description**: Get the shared flow of the authentication state.
- **Returns**: A `SharedFlow<AuthenticationState>` that emits the current authentication state.

### suspend fun isLoggedIn(context: Context): Boolean

- **Description**: Check if the user is currently logged in.
- **Parameters**:
  - `context: Context`: The application context.
- **Returns**: `true` if the user is logged in, `false` otherwise.

### suspend fun isLoggedInStateFlow(context: Context)

- **Description**: Handle the initial authentication flow to check if the user is authenticated or not.
- **Parameters**:
  - `context: Context`: The application context.
- **Emissions**:
  - `AuthenticationState.Loading`: The application is loading the authentication state.
  - `AuthenticationState.Authenticated`: The user is authenticated to access the application.
  - `AuthenticationState.Initial`: The user is not authenticated to access the application.
  - `AuthenticationState.Error`: An error occurred during the authentication process.

### suspend fun initializeAuthentication(context: Context)

- **Description**: Initialize the authentication process.
- **Parameters**:
  - `context: Context`: The application context.
- **Emissions**:
  - `AuthenticationState.Loading`: The application is loading the authentication state.
  - `AuthenticationState.Unauthenticated`: The user is not authenticated to access the application.
  - `AuthenticationState.Error`: An error occurred during the authentication process.

### suspend fun authenticateWithUsernameAndPassword(context: Context, authenticatorId: String, username: String, password: String)

- **Description**: Authenticate the user with the provided username and password.
- **Parameters**:
  - `context: Context`: The application context.
  - `authenticatorId: String`: The ID of the selected authenticator.
  - `username: String`: The username of the user.
  - `password: String`: The password of the user.
- **Emissions**:
  - `AuthenticationState.Loading`: The application is loading the authentication state.
  - `AuthenticationState.Authenticated`: The user is authenticated to access the application.
  - `AuthenticationState.Unauthenticated`: The user is not authenticated to access the application.
  - `AuthenticationState.Error`: An error occurred during the authentication process.

### suspend fun authenticateWithTotp(context: Context, authenticatorId: String, token: String)

- **Description**: Authenticate the user with the provided TOTP (Time-based One-Time Password) token.
- **Parameters**:
  - `context: Context`: The application context.
  - `authenticatorId: String`: The ID of the selected authenticator.
  - `token: String`: The TOTP token provided by the user.
- **Emissions**:
  - `AuthenticationState.Loading`: The application is loading the authentication state.
  - `AuthenticationState.Authenticated`: The user is authenticated to access the application.
  - `AuthenticationState.Unauthenticated`: The user is not authenticated to access the application.
  - `AuthenticationState.Error`: An error occurred during the authentication process.

### suspend fun authenticateWithOpenIdConnect(context: Context, authenticatorId: String)

- **Description**: Authenticate the user with the OpenID Connect authenticator.
- **Parameters**:
  - `context: Context`: The application context.
  - `authenticatorId: String`: The ID of the selected authenticator.
- **Emissions**:
  - `AuthenticationState.Loading`: The application is loading the authentication state.
  - `AuthenticationState.Authenticated`: The user is authenticated to access the application.
  - `AuthenticationState.Unauthenticated`: The user is not authenticated to access the application.
  - `AuthenticationState.Error`: An error occurred during the authentication process.

### suspend fun authenticateWithMicrosoftRedirect(context: Context, authenticatorId: String)

- **Description**: Authenticate the user with the Microsoft authenticator using a redirect flow.
- **Parameters**:
  - `context: Context`: The application context.
  - `authenticatorId: String`: The ID of the selected authenticator.
- **Emissions**:
  - `AuthenticationState.Loading`: The application is loading the authentication state.
  - `AuthenticationState.Authenticated`: The user is authenticated to access the application.
  - `AuthenticationState.Unauthenticated`: The user is not authenticated to access the application.
  - `AuthenticationState.Error`: An error occurred during the authentication process.

### suspend fun authenticateWithGoogle(context: Context, authenticatorId: String)

- **Description**: Authenticate the user with the Google authenticator using the recommended Credential Management API (requires API level 34 or higher).
- **Parameters**:
  - `context: Context`: The application context.
  - `authenticatorId: String`: The ID of the selected authenticator.
- **Emissions**:
  - `AuthenticationState.Loading`: The application is loading the authentication state.
  - `AuthenticationState.Error`: An error occurred during the authentication process.

### suspend fun authenticateWithGoogleLegacy(context: Context, authenticatorId: String, googleAuthenticateResultLauncher: ActivityResultLauncher<Intent>)

- **Description**: Authenticate the user with the Google authenticator using the legacy one-tap method (not recommended for newer API versions).
- **Parameters**:
  - `context: Context`: The application context.
  - `authenticatorId: String`: The ID of the selected authenticator.
  - `googleAuthenticateResultLauncher: ActivityResultLauncher<Intent>`: The result launcher for the Google authentication process.
- **Emissions**:
  - `AuthenticationState.Loading`: The application is loading the authentication state.
  - `AuthenticationState.Error`: An error occurred during the authentication process.

### suspend fun handleGoogleNativeLegacyAuthenticateResult(context: Context, resultCode: Int, data: Intent)

- **Description**: Handle the result of the Google authentication process for the legacy one-tap method.
- **Parameters**:
  - `context: Context`: The application context.
  - `resultCode: Int`: The result code of the Google authentication process.
  - `data: Intent`: The `Intent` object containing the result data of the Google authentication process.
- **Emissions**:
  - `AuthenticationState.Error`: An error occurred during the authentication process.
  - `AuthenticationState.Authenticated`: The user is authenticated to access the application.
  - `AuthenticationState.Unauthenticated`: The user is not authenticated to access the application.

### suspend fun authenticateWithGithubRedirect(context: Context, authenticatorId: String)

- **Description**: Authenticate the user with the GitHub authenticator using a redirect flow.
- **Parameters**:
  - `context: Context`: The application context.
  - `authenticatorId: String`: The ID of the selected authenticator.
- **Emissions**:
  - `AuthenticationState.Loading`: The application is loading the authentication state.
  - `AuthenticationState.Authenticated`: The user is authenticated to access the application.
  - `AuthenticationState.Unauthenticated`: The user is not authenticated to access the application.
  - `AuthenticationState.Error`: An error occurred during the authentication process.

### suspend fun authenticateWithPasskey(context: Context, authenticatorId: String, allowCredentials: List<String>?, timeout: Long?, userVerification: String?)

- **Description**: Authenticate the user with the Passkey authenticator (requires API level 27 or higher).
- **Parameters**:
  - `context: Context`: The application context.
  - `authenticatorId: String`: The ID of the selected authenticator.
  - `allowCredentials: List<String>?`: (Optional) The list of allowed credentials. Default is an empty array.
  - `timeout: Long?`: (Optional) The timeout for the authentication process in milliseconds. Default is 300000 (5 minutes).
  - `userVerification: String?`: (Optional) The user verification method. Default is "required".
- **Emissions**:
  - `AuthenticationState.Loading`: The application is loading the authentication state.
  - `AuthenticationState.Authenticated`: The user is authenticated
  - `AuthenticationState.Unauthenticated`: The user is not authenticated to access the application.
  - `AuthenticationState.Error`: An error occurred during the authentication process.
 
### suspend fun authenticate(context: Context, authenticatorId: String, authenticatorTypeString: String, authParams: LinkedHashMap<String, String>)

- **Description**: Authenticate the user with any selected authenticator.
- **Parameters**:
  - `context: Context`: The application context.
  - `authenticatorId: String`: The ID of the selected authenticator.
  - `authenticatorTypeString: String`: The authenticator type of the selected authenticator.
  - `authenticatorId: String`: The authentication parameters of the selected authenticator as a `LinkedHashMap<String, String>` with the key as the parameter name and the value as the parameter value.

- **Emissions**:
  - `AuthenticationState.Loading`: The application is loading the authentication state.
  - `AuthenticationState.Authenticated`: The user is authenticated.
  - `AuthenticationState.Unauthenticated`: The user is not authenticated to access the application.
  - `AuthenticationState.Error`: An error occurred during the authentication process.
 
### suspend fun getUserDetails(context: Context)

- **Description**: Get the user details of the authenticated user.
- **Parameters**:
  - `context: Context`: The application context.

- **Return**:
  - The user details `LinkedHashMap` that contains the user details

### suspend fun logout(context: Context)

- **Description**: Logout the user from the application.
- **Parameters**:
  - `context: Context`: The application context.

- **Emissions**:
  - `AuthenticationState.Loading`: The application is loading the authentication state.
  - `AuthenticationState.Initial`: The user is not authenticated to access the application, and need to restart the authentication process.
  - `AuthenticationState.Error`: An error occurred during the authentication process.

 Sure, here's a detailed API reference documentation for the provided `TokenProvider` interface:

# TokenProvider

The `TokenProvider` provides functionality to get, validate, refresh, and clear tokens.

```
val tokenProvider:TokenProvider = asgardeoAuth.getTokenProvider()
```

## Methods

### suspend fun getAccessToken(context: Context): String?

- **Description**: Get the access token from the token.
- **Parameters**:
  - `context: Context`: The `Context` instance.
- **Returns**: The access token `String`, or `null` if not available.

### suspend fun getRefreshToken(context: Context): String?

- **Description**: Get the refresh token from the token.
- **Parameters**:
  - `context: Context`: The `Context` instance.
- **Returns**: The refresh token `String`, or `null` if not available.

### suspend fun getIDToken(context: Context): String?

- **Description**: Get the ID token from the token.
- **Parameters**:
  - `context: Context`: The `Context` instance.
- **Returns**: The ID token `String`, or `null` if not available.

### suspend fun getAccessTokenExpirationTime(context: Context): Long?

- **Description**: Get the access token expiration time from the token.
- **Parameters**:
  - `context: Context`: The `Context` instance.
- **Returns**: The access token expiration time `Long`, or `null` if not available.

### suspend fun getScope(context: Context): String?

- **Description**: Get the scope from the token.
- **Parameters**:
  - `context: Context`: The `Context` instance.
- **Returns**: The scope `String`, or `null` if not available.

### suspend fun validateAccessToken(context: Context): Boolean?

- **Description**: Validate the access token by checking the expiration time and whether it is null or empty. **Note: This method does not call the introspection endpoint to validate the access token!**
- **Parameters**:
  - `context: Context`: The `Context` instance.
- **Returns**: `true` if the access token is valid, `false` otherwise, or `null` if an error occurs.

### suspend fun performRefreshTokenGrant(context: Context)

- **Description**: Perform an action with the tokens. If the token is expired, it will perform the refresh the tokens, and then perform the action. This will also update the token in the data store as well.
- **Parameters**:
  - `context: Context`: The `Context` instance.

### suspend fun performAction(context: Context, action: suspend (String?, String?) -> Unit)

- **Description**: Perform an action with fresh tokens. This method will perform the action with fresh tokens and save the updated token state in the data store. The developer can directly use this method to perform an action with fresh tokens, without worrying about refreshing the tokens. If this action fails, it will throw an Exception.
- **Parameters**:
  - `context: Context`: The `Context` instance.
  - `action: suspend (String?, String?) -> Unit`: The action to perform. It will be provided with the access token (`String?`) and the ID token (`String?`).

### suspend fun clearTokens(context: Context): Unit?

- **Description**: Clear the tokens from the token data store. This method will clear the tokens from the data store. After calling this method, the developer needs to perform the authorization flow again to get the tokens.
- **Parameters**:
  - `context: Context`: The `Context` instance.
- **Returns**: `Unit?`
