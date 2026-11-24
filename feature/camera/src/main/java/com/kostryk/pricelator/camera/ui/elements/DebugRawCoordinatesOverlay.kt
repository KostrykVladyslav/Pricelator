package com.kostryk.pricelator.camera.ui.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kostryk.pricelator.camera.ml.DetectedTextBlock

@Composable
fun DebugRawCoordinatesOverlay(
    detectedBlocks: List<DetectedTextBlock>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    val screenWidth = LocalWindowInfo.current.containerSize
    val screenHeight = LocalWindowInfo.current.containerSize

    Canvas(modifier = modifier.fillMaxSize()) {
        // Draw text blocks with coordinates (bitmap is already screen size)
        detectedBlocks.forEach { block ->
            val rect = block.boundingBox

            // Use coordinates directly (no scaling needed)
            val left = rect.left.toFloat()
            val top = rect.top.toFloat()
            val right = rect.right.toFloat()
            val bottom = rect.bottom.toFloat()

            // Draw rectangle
            val color = if (block.isPrice) Color.Green else Color.Red
            drawRect(
                color = color,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                style = Stroke(width = 4f)
            )

            // Draw text label
            val label = "${block.text} ${if (block.isPrice) "✓" else "✗"}"
            val textLayoutResult = textMeasurer.measure(
                text = label,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            // Draw background for text
            val textPadding = 4f
            val bgTop = (top - textLayoutResult.size.height - textPadding * 2).coerceAtLeast(0f)
            val bgWidth = textLayoutResult.size.width + textPadding * 2
            val bgHeight = textLayoutResult.size.height.toFloat() + textPadding * 2

            drawRect(
                color = color.copy(alpha = 0.8f),
                topLeft = Offset(left, bgTop),
                size = Size(bgWidth, bgHeight)
            )

            // Draw text
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(left + textPadding, bgTop + textPadding)
            )
        }

        // Draw info text
        val infoText = "Yellow = Bitmap bounds\nBitmap: Screen: ${screenWidth}x${screenHeight}\n"
        val infoLayoutResult = textMeasurer.measure(
            text = infoText,
            style = TextStyle(
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        )

        drawRect(
            color = Color.Black.copy(alpha = 0.7f),
            topLeft = Offset(10f, 10f),
            size = Size(
                infoLayoutResult.size.width + 20f,
                infoLayoutResult.size.height + 20f
            )
        )

        drawText(
            textLayoutResult = infoLayoutResult,
            topLeft = Offset(20f, 20f)
        )
    }
}
