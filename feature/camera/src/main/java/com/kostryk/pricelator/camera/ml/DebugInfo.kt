package com.kostryk.pricelator.camera.ml

data class DebugInfo(
    val blocks: List<DetectedTextBlock>,
    val imageWidth: Int,
    val imageHeight: Int,
    val rotation: Int,
)