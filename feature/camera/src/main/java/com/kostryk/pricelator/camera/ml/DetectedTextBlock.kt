package com.kostryk.pricelator.camera.ml

import android.graphics.Rect

data class DetectedTextBlock(
    val text: String,
    val boundingBox: Rect,
    val isPrice: Boolean
)