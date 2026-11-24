package com.kostryk.pricelator.camera.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.kostryk.pricelator.camera.ui.alert.AlertRequestPermission
import com.kostryk.pricelator.camera.ui.elements.AnimatedGradientBox
import com.kostryk.pricelator.camera.ui.elements.CameraPreviewContent
import com.kostryk.pricelator.camera.ui.elements.ConversionResultCard
import com.kostryk.pricelator.camera.ui.elements.DebugRawCoordinatesOverlay
import com.kostryk.pricelator.camera.ui.elements.PriceTagOverlay
import com.kostryk.pricelator.core.ui.R
import com.kostryk.pricelator.core.ui.components.CurrencySelectorSheet
import com.kostryk.pricelator.settings.BuildConfig

enum class CurrencySelector {
    NONE, SOURCE, TARGET
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraContent(
    viewModel: CameraViewModel,
    modifier: Modifier = Modifier,
    onSettingsPressed: () -> Unit
) {
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.refreshSettings()
    }

    var componentHeight by remember { mutableStateOf(0.dp) }
    var selectedCurrencySelector by remember { mutableStateOf(CurrencySelector.NONE) }
    val state by viewModel.state.collectAsState()
    val debugMode = remember { BuildConfig.DEBUG }

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    if (cameraPermissionState.status.isGranted) {
        CameraPreviewContent(
            viewModel = viewModel,
            modifier = modifier,
            flashLight = state.isFlashlightOn,
            debugMode = debugMode,
        )

        // Price Tag Detection Overlay (debug mode only)
        if (debugMode && state.priceTagResults.isNotEmpty()) {
            PriceTagOverlay(
                priceTagResults = state.priceTagResults,
                imageWidth = state.priceTagImageWidth,
                imageHeight = state.priceTagImageHeight,
                modifier = modifier
            )
        }

        // Debug: Show Price Tag status indicator
        if (debugMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = "PriceTag: ${state.priceTagResults.size} tags | img: ${state.priceTagImageWidth}x${state.priceTagImageHeight}",
                    color = Color(0xFFFF9800),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .padding(8.dp)
                )
            }
        }

        // Debug: Show RAW coordinates overlay (blocks in bitmap coordinate space)
        if (debugMode && state.debugBlocks.isNotEmpty()) {
            DebugRawCoordinatesOverlay(
                detectedBlocks = state.debugBlocks,
                modifier = modifier
            )
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .onGloballyPositioned { componentHeight = it.size.height.dp },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier.height(height = (componentHeight.value / 4).dp))

            AnimatedVisibility(state.isScanning) {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .heightIn(min = 76.dp, max = 200.dp)
                        .background(
                            color = colorResource(R.color.white_60),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(shape = RoundedCornerShape(16))
                ) {
                    Column(
                        modifier = modifier.padding(16.dp)
                    ) {
                        BoxWithConstraints(
                            modifier = modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedGradientBox(
                                modifier = Modifier.widthIn(max = maxWidth * 0.75f),
                                cornersSize = 25.dp
                            ) {
                                Row(modifier = modifier) {
                                    Image(
                                        painter = painterResource(R.drawable.ic_stars),
                                        contentDescription = "scanningStars",
                                    )
                                    Spacer(modifier.width(12.dp))
                                    Text(
                                        text = stringResource(R.string.scanning),
                                        fontFamily = FontFamily(Font(R.font.hanken_grotesk_bold)),
                                        color = Color.Black,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(top = 2.dp),
                                    )
                                }
                            }

                            Image(
                                painter = painterResource(if (state.isFlashlightOn) R.drawable.ic_light_on else R.drawable.ic_light_off),
                                contentDescription = "light",
                                modifier = modifier
                                    .clip(CircleShape)
                                    .align(Alignment.CenterEnd)
                                    .clickable(onClick = viewModel::toggleFlashlight)
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                state.detectedAmount != null &&
                        state.convertedAmount != null &&
                        state.sourceCurrency != null &&
                        state.targetCurrency != null
            ) {
                ConversionResultCard(
                    modifier = modifier,
                    detectedAmount = state.detectedAmount ?: return@AnimatedVisibility,
                    convertedAmount = state.convertedAmount!!,
                    sourceCurrency = state.sourceCurrency!!,
                    targetCurrency = state.targetCurrency!!,
                    isFlashlightOn = state.isFlashlightOn,
                    updateDate = state.lastUpdateTime,
                    onSourceCurrencyClick = {
                        selectedCurrencySelector = CurrencySelector.SOURCE
                    },
                    onTargetCurrencyClick = {
                        selectedCurrencySelector = CurrencySelector.TARGET
                    },
                    toggleFlashlight = viewModel::toggleFlashlight
                )
            }
        }
    } else {
        AlertRequestPermission(
            modifier = modifier,
            cameraPermissionState = cameraPermissionState
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 62.dp),
        contentAlignment = Alignment.TopEnd,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_settings),
            contentDescription = "settings",
            modifier = Modifier
                .clip(CircleShape)
                .clickable(onClick = onSettingsPressed)
        )
    }

    when (selectedCurrencySelector) {
        CurrencySelector.SOURCE -> {
            if (state.currencies.isNotEmpty() && state.sourceCurrency != null) {
                CurrencySelectorSheet(
                    currencies = state.currencies,
                    selectedCurrency = state.sourceCurrency!!.symbol,
                    onCurrencySelected = { currencyCode ->
                        viewModel.selectSourceCurrency(currencyCode)
                    },
                    onDismiss = {
                        selectedCurrencySelector = CurrencySelector.NONE
                    }
                )
            }
        }

        CurrencySelector.TARGET -> {
            if (state.currencies.isNotEmpty() && state.targetCurrency != null) {
                CurrencySelectorSheet(
                    currencies = state.currencies,
                    selectedCurrency = state.targetCurrency!!.symbol,
                    onCurrencySelected = { currencyCode ->
                        viewModel.selectTargetCurrency(currencyCode)
                    },
                    onDismiss = {
                        selectedCurrencySelector = CurrencySelector.NONE
                    }
                )
            }
        }

        CurrencySelector.NONE -> {}
    }
}

@Composable
@Preview
private fun CameraContentPreview() {
    CameraPreviewContent(viewModel(), Modifier, false)
}
