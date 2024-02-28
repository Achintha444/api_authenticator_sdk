package io.wso2.android.api_authenticator.sdk.sample.presentation.util.common_component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.wso2.android.api_authenticator.sdk.sample.R

@Composable
fun LogoImage() {
    Column {
        Image(
            painter = painterResource(R.drawable.is_logo),
            contentDescription = stringResource(R.string.common_common_components_logo_logo_placeholder)
        )
    }
}