package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import io.wso2.android.api_authenticator.sdk.sample.R
import io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.AuthScreenViewModel

@Composable
internal fun TotpAuth(
    viewModel: AuthScreenViewModel = hiltViewModel(),
    authenticatorType: io.wso2.android.api_authenticator.sdk.models.autheniticator.AuthenticatorType
) {
    TotpAuthComponent(
        onSubmit = { token ->
            viewModel.authenticateWithTotp(authenticatorType.authenticatorId, token)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetWithForm(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (totp: String) -> Unit
) {
    var totpCode by remember { mutableStateOf("") }

    if (isOpen) {
        ModalBottomSheet(
            onDismissRequest = {
                onDismiss()
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.screens_auth_screen_totp_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.screens_auth_screen_totp_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = totpCode,
                    onValueChange = { totpCode = it },
                    label = { Text(text = stringResource(id = R.string.screens_auth_screen_totp_code)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        onSubmit(totpCode)
                        onDismiss()
                    },
                    modifier = Modifier
                        .align(alignment = Alignment.CenterHorizontally)
                        .padding(end = 24.dp)
                ) {
                    Text(text = stringResource(id = R.string.screens_auth_screen_totp_submit))
                }
            }
        }
    }
}

@Composable
fun TotpAuthComponent(
    onSubmit: (totp: String) -> Unit
) {
    var bottomSheetOpen by remember { mutableStateOf(false) }

    Button(
        onClick = { bottomSheetOpen = true },
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = stringResource(id = R.string.screens_auth_screen_totp_login))
    }

    BottomSheetWithForm(
        isOpen = bottomSheetOpen,
        onDismiss = { bottomSheetOpen = false },
        onSubmit = onSubmit
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TotpAuthPreview() {
    TotpAuthComponent(onSubmit = {})
}
