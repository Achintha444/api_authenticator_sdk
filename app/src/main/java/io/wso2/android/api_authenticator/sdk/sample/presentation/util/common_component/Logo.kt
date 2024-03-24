package io.wso2.android.api_authenticator.sdk.sample.presentation.util.common_component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.wso2.android.api_authenticator.sdk.sample.R

@Composable
private fun Logo(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoImage()
            SubTitle(fontSize)
        }
    }

}

@Composable
private fun SubTitle(fontSize: TextUnit) {
    Text(
        text = stringResource(R.string.common_common_components_logo_subtitle),
        fontSize = fontSize
    )
}

@Composable
fun LogoLarge(
    modifier: Modifier = Modifier
) {
    Logo(modifier)
}

@Composable
fun LogoSmall() {
    Logo(
        modifier = Modifier
            .height(75.dp)
            .width(250.dp),
        fontSize = 10.sp
    )
}
