package com.kostryk.pricelator.camera.ml

import android.graphics.RectF

data class PriceTagWithText(
    val boundingBox: RectF,
    val confidence: Float,
    val recognizedText: String?
)