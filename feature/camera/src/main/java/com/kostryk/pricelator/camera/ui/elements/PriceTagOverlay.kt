package com.kostryk.pricelator.camera.ui.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.kostryk.pricelator.camera.ui.PriceTagResult

@Composable
fun PriceTagOverlay(
    priceTagResults: List<PriceTagResult>,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier
) {
    val textSize = with(LocalDensity.current) { 16.sp.toPx() }
    val labelTextSize = with(LocalDensity.current) { 12.sp.toPx() }

    Canvas(modifier = modifier.fillMaxSize()) {
        if (imageWidth == 0 || imageHeight == 0) return@Canvas

        val scaleX = size.width / imageWidth
        val scaleY = size.height / imageHeight

        for (result in priceTagResults) {
            val box = result.boundingBox
            val left = box.left * scaleX
            val top = box.top * scaleY
            val right = box.right * scaleX
            val bottom = box.bottom * scaleY
            val boxWidth = right - left
            val boxHeight = bottom - top

            // Draw bounding box with rounded corners
            drawRoundRect(
                color = Color(0xFFFF9800), // Orange
                topLeft = Offset(left, top),
                size = Size(boxWidth, boxHeight),
                cornerRadius = CornerRadius(8f, 8f),
                style = Stroke(
                    width = 4f,
                    pathEffect = PathEffect.cornerPathEffect(8f)
                )
            )

            // Draw confidence label background
            val confidenceText = "${(result.confidence * 100).toInt()}%"
            val labelPaint = android.graphics.Paint().apply {
                this.textSize = labelTextSize
                this.color = android.graphics.Color.WHITE
                isAntiAlias = true
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            val labelWidth = labelPaint.measureText(confidenceText) + 16f
            val labelHeight = labelTextSize + 8f

            // Label at top-left corner
            drawRoundRect(
                color = Color(0xFFFF9800),
                topLeft = Offset(left, top - labelHeight - 4f),
                size = Size(labelWidth, labelHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Draw confidence text
            drawContext.canvas.nativeCanvas.drawText(
                confidenceText,
                left + 8f,
                top - 10f,
                labelPaint
            )

            // Draw recognized text if available
            if (!result.recognizedText.isNullOrBlank()) {
                val textPaint = android.graphics.Paint().apply {
                    this.textSize = textSize
                    this.color = android.graphics.Color.WHITE
                    isAntiAlias = true
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    setShadowLayer(4f, 2f, 2f, android.graphics.Color.BLACK)
                }

                val priceText = result.recognizedText!!
                val priceTextWidth = textPaint.measureText(priceText)
                val priceTextHeight = textSize + 12f

                // Background for price text at bottom of box
                val textBgTop = bottom + 4f
                drawRoundRect(
                    color = Color(0xFF4CAF50).copy(alpha = 0.9f), // Green
                    topLeft = Offset(left, textBgTop),
                    size = Size(
                        maxOf(priceTextWidth + 20f, boxWidth),
                        priceTextHeight
                    ),
                    cornerRadius = CornerRadius(6f, 6f)
                )

                // Draw price text
                drawContext.canvas.nativeCanvas.drawText(
                    priceText,
                    left + 10f,
                    textBgTop + textSize + 2f,
                    textPaint
                )
            }
        }
    }
}
