package com.kostryk.pricelator.camera.ml

data class ScanRegion(
    val widthPercent: Float = 0.9f,
    val heightPercent: Float = 0.25f,
    val verticalOffsetPercent: Float = -0.025f
)