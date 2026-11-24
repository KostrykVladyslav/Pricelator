package com.kostryk.pricelator.camera.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CameraScreen(onSettingsPressed: () -> Unit) {
    val viewModel = hiltViewModel<CameraViewModel>()
    CameraContent(viewModel = viewModel, onSettingsPressed = onSettingsPressed)
}