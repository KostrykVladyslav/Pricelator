@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.kostryk.pricelator.camera.ui.elements

import android.util.Log
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kostryk.pricelator.camera.ml.DigitRecognizer
import com.kostryk.pricelator.camera.ml.PriceTagAnalyzer
import com.kostryk.pricelator.camera.ml.PriceTagDetector
import com.kostryk.pricelator.camera.ui.CameraViewModel
import com.kostryk.pricelator.camera.ui.PriceTagResult
import com.kostryk.pricelator.settings.BuildConfig
import java.util.concurrent.Executors

@Composable
fun CameraPreviewContent(
    viewModel: CameraViewModel,
    modifier: Modifier = Modifier,
    flashLight: Boolean,
    debugMode: Boolean = BuildConfig.DEBUG,
) {
    val isPreview = LocalInspectionMode.current
    if (isPreview) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFE0E0E0))
        )
        CameraOverlay()
    } else {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        val analysisExecutor = remember {
            Executors.newSingleThreadExecutor()
        }

        val cameraController = remember {
            LifecycleCameraController(context)
        }

        val configuration = LocalConfiguration.current
        val density = LocalDensity.current

        val screenWidthPx = with(density) { configuration.screenWidthDp.dp.roundToPx() }
        val screenHeightPx = with(density) { configuration.screenHeightDp.dp.roundToPx() }

        val priceTagDetector = remember {
            try {
                PriceTagDetector(context)
            } catch (e: Exception) {
                Log.e("CameraPreview", "Failed to create PriceTagDetector", e)
                null
            }
        }

        val digitRecognizer = remember {
            try {
                DigitRecognizer(context)
            } catch (e: Exception) {
                Log.e("CameraPreview", "Failed to create DigitRecognizer", e)
                null
            }
        }

        val analyzer = remember(screenWidthPx, screenHeightPx, priceTagDetector, digitRecognizer) {
            PriceTagAnalyzer(
                onDetectedTextUpdated = viewModel::onTextDetected,
                priceTagDetector = priceTagDetector,
                digitRecognizer = digitRecognizer,
                onPriceTagResults = if (priceTagDetector != null) { priceTagsWithText, imageWidth, imageHeight ->
                    val results = priceTagsWithText.map { tag ->
                        PriceTagResult(
                            boundingBox = tag.boundingBox,
                            confidence = tag.confidence,
                            recognizedText = tag.recognizedText
                        )
                    }
                    viewModel.onPriceTagResults(results, imageWidth, imageHeight)
                } else null,
                screenHeight = screenHeightPx,
                screenWidth = screenWidthPx
            )
        }

        DisposableEffect(Unit) {
            onDispose {
                analyzer.close()
                priceTagDetector?.close()
                digitRecognizer?.close()
                analysisExecutor.shutdown()
            }
        }

        LaunchedEffect(flashLight) {
            cameraController.enableTorch(flashLight)
        }

        LaunchedEffect(Unit) {
            cameraController.setImageAnalysisAnalyzer(analysisExecutor, analyzer)
        }

        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.PERFORMANCE
                    controller = cameraController
                }.also {
                    cameraController.bindToLifecycle(lifecycleOwner)
                }
            },
            onRelease = {
                cameraController.unbind()
            }
        )
        CameraOverlay()
    }
}
