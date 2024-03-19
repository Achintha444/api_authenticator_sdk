package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.home_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import io.wso2.android.api_authenticator.sdk.sample.R
import io.wso2.android.api_authenticator.sdk.sample.domain.model.UserDetails
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.AuthScreenState
import io.wso2.android.api_authenticator.sdk.sample.presentation.util.common_component.LoadingDialog
import io.wso2.android.api_authenticator.sdk.sample.presentation.util.common_component.LogoSmall
import io.wso2.android.api_authenticator.sdk.sample.ui.theme.Api_authenticator_sdkTheme

@Composable
internal fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    HomeScreenContent(state.value)
}

@Composable
fun HomeScreenContent(
    state: HomeScreenState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LoadingDialog(isLoading = state.isLoading)
        LogoSmall()
        UserCard(
            imageUrl = state.user?.imageUrl,
            email = state.user?.email,
            username = state.user?.username,
            firstName = state.user?.firstName,
            lastName = state.user?.lastName
        )
        LoginSuccessMessage()
        LogoutButton(Modifier)
    }
}

@Composable
private fun LoginSuccessMessage() {
    Text(
        text = stringResource(R.string.screens_home_screen_login_success)
    )
}

@Composable
private fun UserCard(
    imageUrl: String?,
    email: String?,
    username: String?,
    firstName: String?,
    lastName: String?
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            if (imageUrl != null) {
                Image(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = stringResource(id = R.string.screens_home_screen_user_card_user_image_description),
                    contentScale = ContentScale.FillBounds
                )
            }
            Spacer(modifier = Modifier.size(16.dp))

            // User information column
            Column(modifier = Modifier.fillMaxWidth()) {
                if (username != null) {
                    UserField(
                        label = stringResource(id = R.string.screens_home_screen_user_card_username),
                        value = username
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                }
                if (email != null) {
                    UserField(
                        label = stringResource(id = R.string.screens_home_screen_user_card_email),
                        value = email
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                }
                if (firstName != null) {
                    UserField(
                        label = stringResource(id = R.string.screens_home_screen_user_card_first_name),
                        value = firstName
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                }
                if (lastName != null) {
                    UserField(
                        label = stringResource(id = R.string.screens_home_screen_user_card_last_name),
                        value = lastName
                    )
                }
            }
        }
    }
}

@Composable
private fun UserField(
    label: String,
    value: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.padding(horizontal = 2.dp))
        Text(text = value, style = MaterialTheme.typography.bodySmall)
    }
}


@Composable
private fun LogoutButton(modifier: Modifier = Modifier) {
    Button(
        modifier = modifier,
        onClick = { /*TODO*/ }
    ) {
        Text(text = stringResource(R.string.common_logout))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun HomeScreenPreview() {
    Api_authenticator_sdkTheme {
        HomeScreenContent(
            HomeScreenState(
                isLoading = false,
                user = UserDetails(
                    imageUrl = "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?q=80&w=3570&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                    username = "JohnDoe",
                    email = "john@wso2.com",
                    firstName = "John",
                    lastName = "Doe"
                )
            )
        )
    }
}