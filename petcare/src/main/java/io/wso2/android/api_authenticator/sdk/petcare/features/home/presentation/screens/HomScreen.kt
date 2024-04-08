package io.wso2.android.api_authenticator.sdk.petcare.features.home.presentation.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.wso2.android.api_authenticator.sdk.petcare.R
import io.wso2.android.api_authenticator.sdk.petcare.features.home.domain.models.UserDetails
import io.wso2.android.api_authenticator.sdk.petcare.features.home.presentation.util.AddPetFab
import io.wso2.android.api_authenticator.sdk.petcare.features.home.presentation.util.DoctorSearchField
import io.wso2.android.api_authenticator.sdk.petcare.ui.theme.Api_authenticator_sdkTheme
import io.wso2.android.api_authenticator.sdk.petcare.util.ui.UiUtil

@Composable
internal fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    HomeScreenContent(state.value)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun HomeScreenContent(
    state: HomeScreenState
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.home_logo),
                            modifier = Modifier
                                .size(UiUtil.getScreenHeight().dp / 6),
                            contentDescription = "Home Logo",

                            )
                        Icon(
                            imageVector = Icons.Rounded.AccountCircle,
                            contentDescription = "Menu",
                            modifier = Modifier
                                .size(UiUtil.getScreenHeight().dp / 25),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            )
        },
        floatingActionButton = { AddPetFab() },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            DoctorSearchField()
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .height(UiUtil.getScreenHeight().dp / 5)
                            .offset(x = 8.dp)
                            .align(Alignment.CenterEnd)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.person_dog_home),
                            contentDescription = "Person with dog"
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 24.dp, bottom = 16.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SuggestionChip(
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    border = null,
                                    modifier = Modifier.height(16.dp),
                                    onClick = {},
                                    label = {
                                        Text(
                                            text = "LIVE",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error,
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Consult with",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.surface
                                )
                            }
                            Column(
                                verticalArrangement = Arrangement.spacedBy(0.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "an ",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.surface
                                    )
                                    Text(
                                        text = "Experienced",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.surface,
                                        modifier = Modifier.width(200.dp)
                                    )
                                }
                                Text(
                                    text = "Veterinarian",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.offset(y = -6.dp)
                                )
                            }

                        }
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = "Consult Now",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.video_call),
                                    contentDescription = "Menu",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .offset(x = 4.dp),
                                )
                            },
                            shape = MaterialTheme.shapes.extraSmall,
                            border = null,
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }
                }
            }
        }
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
