package com.kostryk.pricelator.camera.ui.elements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType

@Composable
fun CameraOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val outerRect = Rect(0f, 0f, size.width, size.height)

        val hole = RoundRect(
            left = size.width * 0.05f,
            top = size.height * 0.35f,
            right = size.width * 0.95f,
            bottom = size.height * 0.6f,
            cornerRadius = CornerRadius(40f, 40f)
        )

        val path = Path().apply {
            fillType = PathFillType.EvenOdd
            addRect(outerRect)
            addRoundRect(hole)
        }

        drawPath(
            path = path,
            color = Color.Black.copy(alpha = 0.6f)
        )
    }
}