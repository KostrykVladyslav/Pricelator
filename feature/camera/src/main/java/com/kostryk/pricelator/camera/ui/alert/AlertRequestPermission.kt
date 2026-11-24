package com.kostryk.pricelator.camera.ui.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.shouldShowRationale
import com.kostryk.pricelator.core.ui.R
import com.kostryk.pricelator.ui.theme.backgroundDark

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AlertRequestPermission(modifier: Modifier, cameraPermissionState: PermissionState) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundDark),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
            stringResource(R.string.request_permission_description_rationale)
        } else {
            stringResource(R.string.request_permission_description)
        }

        Text(
            text = stringResource(R.string.request_permission_title),
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.hanken_grotesk_extrabold)),
            color = Color.White,
            fontSize = 32.sp,
            lineHeight = 38.sp,
            modifier = Modifier.width(263.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = textToShow,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily(Font(R.font.hanken_grotesk)),
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier.width(309.dp)
        )

        Spacer(Modifier.height(32.dp))
        Button(
            modifier = Modifier
                .height(50.dp)
                .widthIn(260.dp, 340.dp)
                .clip(CircleShape),
            onClick = { cameraPermissionState.launchPermissionRequest() },
            colors = ButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black,
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.Gray
            )
        ) {
            Text(
                text = stringResource(R.string.grant_access),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.hanken_grotesk_bold)),
                color = Color.Black,
                fontSize = 24.sp
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Preview
private fun Preview() {
    val mockPermissionState = object : PermissionState {
        override val permission: String = android.Manifest.permission.CAMERA
        override val status: PermissionStatus = PermissionStatus.Denied(shouldShowRationale = false)
        override fun launchPermissionRequest() {}
    }

    AlertRequestPermission(
        modifier = Modifier,
        cameraPermissionState = mockPermissionState
    )
}