package io.wso2.android.api_authenticator.sdk.sample.presentation.screens.auth_screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TotpAuth() {
    TotpAuthComponent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetWithForm(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var totpCode by remember { mutableStateOf("") }

    if (isOpen) {
        BottomSheetScaffold(
            sheetContent = {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Enter TOTP Code")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Enter the verification code from your authenticator app.")
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = totpCode,
                        onValueChange = { totpCode = it },
                        label = { Text("TOTP Code") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            onSubmit(totpCode)
                            onDismiss()
                        },
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                    ) {
                        Text("Submit")
                    }
                }
            },
            sheetShape = MaterialTheme.shapes.medium
        ) {}
    }
}

@Composable
fun TotpAuthComponent() {
    var bottomSheetOpen by remember { mutableStateOf(false) }

    BottomSheetWithForm(
        isOpen = bottomSheetOpen,
        onDismiss = { bottomSheetOpen = false },
        onSubmit = { totpCode ->
            // Here you can handle the submitted TOTP code
            // For example, you might want to validate it or perform some action
            // println("Submitted TOTP code: $totpCode")
        }
    )

    Button(
        onClick = { bottomSheetOpen = true },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Open Bottom Sheet")
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TotpAuthPreview() {
    TotpAuth()
}

