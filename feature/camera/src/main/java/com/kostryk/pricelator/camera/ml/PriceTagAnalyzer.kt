package com.kostryk.pricelator.camera.ml

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs
import kotlin.math.max

class PriceTagAnalyzer(
    private val onDetectedTextUpdated: (String) -> Unit,
    private val priceTagDetector: PriceTagDetector? = null,
    private val digitRecognizer: DigitRecognizer? = null,
    private val onPriceTagResults: ((List<PriceTagWithText>, Int, Int) -> Unit)? = null,
    private val confidenceThreshold: Float = 0.1f,
    private val scanRegion: ScanRegion = ScanRegion(),
    private val screenHeight: Int,
    private val screenWidth: Int
) : ImageAnalysis.Analyzer {

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val isProcessing = AtomicBoolean(false)

    private var lastDetectedText: String = ""
    private var currentStableText: String = ""
    private var consecutiveCount: Int = 0
    private val stabilityThreshold: Int = 2

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (!isProcessing.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }

        scope.launch(context = Dispatchers.Unconfined) {
            try {
                val scaledBitmap = imageProxyToBitmap(
                    imageProxy = imageProxy,
                    targetWidth = screenWidth,
                    targetHeight = screenHeight
                ) ?: return@launch

                val cropRect = calculateCropRect(scaledBitmap.width, scaledBitmap.height)

                val croppedBitmap = try {
                    Bitmap.createBitmap(
                        scaledBitmap,
                        cropRect.left.coerceAtLeast(0),
                        cropRect.top.coerceAtLeast(0),
                        cropRect.width().coerceAtMost(scaledBitmap.width - cropRect.left),
                        cropRect.height().coerceAtMost(scaledBitmap.height - cropRect.top)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    scaledBitmap
                }

                runPriceTagDetection(
                    croppedBitmap = croppedBitmap,
                    scaledBitmap = scaledBitmap,
                    cropOffsetX = cropRect.left,
                    cropOffsetY = cropRect.top
                )
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                imageProxy.close()
                isProcessing.set(false)
            }
        }
    }

    private fun runPriceTagDetection(
        croppedBitmap: Bitmap,
        scaledBitmap: Bitmap,
        cropOffsetX: Int,
        cropOffsetY: Int
    ): Boolean {
        val detector = priceTagDetector ?: return false

        try {
            val detectionStart = System.currentTimeMillis()
            val priceTags = detector.detect(croppedBitmap, confidenceThreshold)

            if (priceTags.isEmpty()) {
                onPriceTagResults?.invoke(emptyList(), scaledBitmap.width, scaledBitmap.height)
                return false
            }

            val sortedTags = priceTags.sortedByDescending { it.confidence }
            val priceTagsWithText = sortedTags.mapIndexed { index, priceTag ->
                val box = priceTag.boundingBox

                val adjustedBox = RectF(
                    box.left + cropOffsetX,
                    box.top + cropOffsetY,
                    box.right + cropOffsetX,
                    box.bottom + cropOffsetY
                )

                val ocrText = if (index == 0 && digitRecognizer != null) {
                    val paddingX = (box.width() * 0.1f).toInt()
                    val paddingY = (box.height() * 0.1f).toInt()

                    val tagLeft = (box.left.toInt() - paddingX).coerceIn(0, croppedBitmap.width - 1)
                    val tagTop = (box.top.toInt() - paddingY).coerceIn(0, croppedBitmap.height - 1)
                    val tagRight =
                        (box.right.toInt() + paddingX).coerceIn(tagLeft + 1, croppedBitmap.width)
                    val tagBottom =
                        (box.bottom.toInt() + paddingY).coerceIn(tagTop + 1, croppedBitmap.height)
                    val tagWidth = tagRight - tagLeft
                    val tagHeight = tagBottom - tagTop

                    if (tagWidth > 0 && tagHeight > 0) {
                        try {
                            val tagBitmap = Bitmap.createBitmap(
                                croppedBitmap, tagLeft, tagTop, tagWidth, tagHeight
                            )
                            val result = digitRecognizer.recognize(tagBitmap)
                            if (!result.isNullOrBlank() && result.any { it.isDigit() }) {
                                result
                            } else null
                        } catch (e: Exception) {
                            Log.w(TAG, "OCR failed for price tag", e)
                            null
                        }
                    } else null
                } else null

                PriceTagWithText(
                    boundingBox = adjustedBox,
                    confidence = priceTag.confidence,
                    recognizedText = ocrText
                )
            }

            val totalTime = System.currentTimeMillis() - detectionStart
            Log.d(TAG, "Pipeline: ${priceTagsWithText.size} tags in ${totalTime}ms")

            onPriceTagResults?.invoke(priceTagsWithText, scaledBitmap.width, scaledBitmap.height)

            val bestTagPrice = priceTagsWithText
                .sortedByDescending { it.confidence }
                .firstNotNullOfOrNull { tag ->
                    tag.recognizedText?.let { extractPrice(it) }
                }

            if (bestTagPrice != null) {
                processDetectedText(bestTagPrice)
            }

            return true
        } catch (e: Exception) {
            Log.e(TAG, "Error in price tag detection pipeline", e)
            return false
        }
    }

    private fun calculateCropRect(imageWidth: Int, imageHeight: Int): Rect {
        val centerX = imageWidth / 2f
        val baseCenterY = imageHeight / 2f
        val verticalOffset = imageHeight * scanRegion.verticalOffsetPercent
        val centerY = baseCenterY + verticalOffset

        val scanWidth = (imageWidth * scanRegion.widthPercent).toInt()
        val scanHeight = (imageHeight * scanRegion.heightPercent).toInt()

        val left = (centerX - scanWidth / 2f).toInt()
        val top = (centerY - scanHeight / 2f).toInt()
        val right = (centerX + scanWidth / 2f).toInt()
        val bottom = (centerY + scanHeight / 2f).toInt()

        return Rect(
            left.coerceIn(0, imageWidth),
            top.coerceIn(0, imageHeight),
            right.coerceIn(0, imageWidth),
            bottom.coerceIn(0, imageHeight)
        )
    }

    private fun extractPrice(text: String): String? {
        val cleanText = text.trim()

        val letterCount = cleanText.count { it.isLetter() }
        val digitCount = cleanText.count { it.isDigit() }
        if (letterCount > digitCount && letterCount > 2) {
            return null
        }

        val patterns = listOf(
            Regex("""\b(\d+[.,]\d+)\b"""),
            Regex("""(\d{1,3}[.,]\d{3}[.,]\d{2})"""),
            Regex("""\b(\d+)\s+(\d{2})\b"""),
            Regex("""\D*(\d+[.,]\d+)\D*"""),
            Regex("""[$€£¥₴]\s*(\d+)\b"""),
            Regex("""\b(\d+)\s*[$€£¥₴]"""),
            Regex("""\b(\d{2,})\b"""),
            Regex("""\b([1-9])\b""")
        )

        for (pattern in patterns) {
            val match = pattern.find(cleanText)
            if (match != null) {
                if (match.groupValues.size >= 3 && match.groupValues[2].isNotEmpty()) {
                    return "${match.groupValues[1]}.${match.groupValues[2]}"
                }
                return match.groupValues[1]
            }
        }

        if (cleanText.matches(Regex("""\d+[.,]\d{1,2}"""))) {
            return cleanText
        }

        return null
    }

    private fun normalizePrice(price: String) = price.replace(",", ".")
        .replace(" ", "")
        .replace(Regex("[$€£¥₴]"), "")
        .trim()

    private fun arePricesSimilar(price1: String, price2: String): Boolean {
        val normalized1 = normalizePrice(price1)
        val normalized2 = normalizePrice(price2)

        if (normalized1 == normalized2) return true

        val num1 = normalized1.toDoubleOrNull()
        val num2 = normalized2.toDoubleOrNull()

        if (num1 != null && num2 != null) {
            val tolerance = 0.01
            return abs(num1 - num2) / max(num1, num2) <= tolerance
        }

        return false
    }

    private fun processDetectedText(detectedText: String) {
        if (arePricesSimilar(detectedText, lastDetectedText)) {
            consecutiveCount++

            if (consecutiveCount >= stabilityThreshold && !arePricesSimilar(
                    detectedText,
                    currentStableText
                )
            ) {
                currentStableText = detectedText
                onDetectedTextUpdated(detectedText)
            }
        } else {
            lastDetectedText = detectedText
            consecutiveCount = 1
        }
    }

    @ExperimentalGetImage
    private fun imageProxyToBitmap(
        imageProxy: ImageProxy,
        targetWidth: Int = 1080,
        targetHeight: Int = 2340
    ): Bitmap? {
        return try {
            val bitmap = imageProxy.toBitmap()

            val rotationDegrees = imageProxy.imageInfo.rotationDegrees.toFloat()
            val originalWidth = if (rotationDegrees % 180 == 0f) bitmap.width.toFloat() else bitmap.height.toFloat()
            val originalHeight = if (rotationDegrees % 180 == 0f) bitmap.height.toFloat() else bitmap.width.toFloat()

            val scaleX = targetWidth / originalWidth
            val scaleY = targetHeight / originalHeight
            val scale = maxOf(scaleX, scaleY)

            val matrix = Matrix().apply {
                postRotate(rotationDegrees, bitmap.width / 2f, bitmap.height / 2f)
                postScale(scale, scale, bitmap.width / 2f, bitmap.height / 2f)
            }

            val transformed = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )

            val cropX = ((transformed.width - targetWidth) / 2).coerceAtLeast(0)
            val cropY = ((transformed.height - targetHeight) / 2).coerceAtLeast(0)

            if (cropX == 0 && cropY == 0 &&
                transformed.width == targetWidth && transformed.height == targetHeight
            ) {
                transformed
            } else {
                Bitmap.createBitmap(
                    transformed,
                    cropX,
                    cropY,
                    targetWidth.coerceAtMost(transformed.width),
                    targetHeight.coerceAtMost(transformed.height)
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error converting ImageProxy to Bitmap", e)
            null
        }
    }

    fun close() {
        scope.cancel()
        digitRecognizer?.close()
    }

    companion object {
        private const val TAG = "PriceTagAnalyzer"
    }
}
