package com.kostryk.pricelator.camera.ui.elements

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedGradientBox(
    modifier: Modifier = Modifier,
    cornersSize: Dp = 21.dp,
    content: @Composable (BoxScope.() -> Unit)
) {
    val gradientColors = listOf(
        Color(0xFF00BFFF),
        Color(0xFF8A2BE2),
        Color(0xFF00BFFF),
        Color(0xFF8A2BE2),
        Color(0xFF00BFFF)
    )

    val infiniteTransition = rememberInfiniteTransition(label = "gradient animation")

    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradient angle"
    )

    val angleInRadians = Math.toRadians(angle.toDouble()).toFloat()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(cornersSize))
            .background(Color.White)
            .drawWithCache {
                val radius = size.maxDimension
                val centerX = size.width / 2
                val centerY = size.height / 2

                val startX = centerX + radius * cos(angleInRadians)
                val startY = centerY + radius * sin(angleInRadians)
                val endX = centerX - radius * cos(angleInRadians)
                val endY = centerY - radius * sin(angleInRadians)

                val animatedBrush = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY)
                )

                onDrawWithContent {
                    drawContent()
                    drawRoundRect(
                        brush = animatedBrush,
                        style = Stroke(width = 6.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornersSize.toPx())
                    )
                }
            }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        content = content
    )
}