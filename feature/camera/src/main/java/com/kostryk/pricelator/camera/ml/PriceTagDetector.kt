package com.kostryk.pricelator.camera.ml

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import java.nio.FloatBuffer
import kotlin.math.min

class PriceTagDetector(private val context: Context) {

    private var ortEnvironment: OrtEnvironment? = null
    private var session: OrtSession? = null

    init {
        setupOnnx()
    }

    private fun setupOnnx() {
        ortEnvironment = OrtEnvironment.getEnvironment()
        val modelBytes = context.assets.open(MODEL_NAME).readBytes()

        val options = OrtSession.SessionOptions().apply {
            setIntraOpNumThreads(NUM_THREADS)
            setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
        }

        session = try {
            ortEnvironment?.createSession(modelBytes, options).also {
                Log.i(TAG, "✅ ORT CPU session created (threads=$NUM_THREADS, optLevel=ALL)")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create session: ${e.message}", e)
            null
        }
    }

    fun detect(
        bitmap: Bitmap,
        confidenceThreshold: Float = 0.25f,
        iouThreshold: Float = 0.45f
    ): List<PriceTagBox> {
        val currentSession = session ?: return emptyList()
        val totalStart = System.nanoTime()

        val results = synchronized(currentSession) {
            if (session == null) return emptyList()

            val t0 = System.nanoTime()
            val (resizedBitmap, scale, padX, padY) = letterboxResize(bitmap)
            val t1 = System.nanoTime()
            val inputTensor = preprocessImage(resizedBitmap)
            val t2 = System.nanoTime()
            val inputName = currentSession.inputNames.first()

            val sessionResults = currentSession.run(mapOf(inputName to inputTensor))
            val t3 = System.nanoTime()

            val outputTensor = sessionResults.first().value as OnnxTensor
            val outputShape = outputTensor.info.shape

            val floatBuffer = outputTensor.floatBuffer
            val output = FloatArray(floatBuffer.remaining())
            floatBuffer.get(output)

            inputTensor.close()
            outputTensor.close()
            sessionResults.close()

            Log.d(TAG, "⏱ letterbox=${(t1-t0)/1_000_000}ms preprocess=${(t2-t1)/1_000_000}ms inference=${(t3-t2)/1_000_000}ms")

            Triple(output, outputShape, Triple(scale, padX, padY))
        }

        val (output, outputShape, scaleInfo) = results
        val (scale, padX, padY) = scaleInfo

        val postResult = postprocess(
            output, outputShape,
            bitmap.width, bitmap.height,
            scale, padX, padY,
            confidenceThreshold
        )

        val totalMs = (System.nanoTime() - totalStart) / 1_000_000
        Log.d(TAG, "⏱ TOTAL detect=${totalMs}ms (${postResult.size} tags)")

        return postResult
    }

    private fun letterboxResize(bitmap: Bitmap): LetterboxResult {
        val originalWidth = bitmap.width.toFloat()
        val originalHeight = bitmap.height.toFloat()

        val scale = min(INPUT_SIZE / originalWidth, INPUT_SIZE / originalHeight)
        val newWidth = (originalWidth * scale).toInt()
        val newHeight = (originalHeight * scale).toInt()

        val padX = (INPUT_SIZE - newWidth) / 2
        val padY = (INPUT_SIZE - newHeight) / 2

        val scaledBitmap = bitmap.scale(newWidth, newHeight)
        val letterboxed = createBitmap(INPUT_SIZE, INPUT_SIZE)
        val canvas = android.graphics.Canvas(letterboxed)
        canvas.drawColor(android.graphics.Color.rgb(114, 114, 114))
        canvas.drawBitmap(scaledBitmap, padX.toFloat(), padY.toFloat(), null)

        return LetterboxResult(letterboxed, scale, padX, padY)
    }

    private fun preprocessImage(bitmap: Bitmap): OnnxTensor {
        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        bitmap.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE)

        val floatBuffer = FloatBuffer.allocate(1 * 3 * INPUT_SIZE * INPUT_SIZE)

        for (c in 0 until 3) {
            val shift = (2 - c) * 8
            for (i in pixels.indices) {
                floatBuffer.put((pixels[i] shr shift and 0xFF) / 255.0f)
            }
        }
        floatBuffer.rewind()

        return OnnxTensor.createTensor(
            ortEnvironment,
            floatBuffer,
            longArrayOf(1, 3, INPUT_SIZE.toLong(), INPUT_SIZE.toLong())
        )
    }

    private fun postprocess(
        output: FloatArray,
        shape: LongArray,
        originalWidth: Int,
        originalHeight: Int,
        scale: Float,
        padX: Int,
        padY: Int,
        confidenceThreshold: Float
    ): List<PriceTagBox> {
        val detections = mutableListOf<PriceTagBox>()

        val numDetections = shape[1].toInt()
        val numFeatures = shape[2].toInt()

        for (i in 0 until numDetections) {
            val baseIdx = i * numFeatures

            val x1Model = output[baseIdx]
            val y1Model = output[baseIdx + 1]
            val x2Model = output[baseIdx + 2]
            val y2Model = output[baseIdx + 3]
            val confidence = output[baseIdx + 4]

            if (confidence < confidenceThreshold) continue
            if (x2Model <= x1Model || y2Model <= y1Model) continue

            val x1 = (x1Model - padX) / scale
            val y1 = (y1Model - padY) / scale
            val x2 = (x2Model - padX) / scale
            val y2 = (y2Model - padY) / scale

            val box = RectF(
                x1.coerceIn(0f, originalWidth.toFloat()),
                y1.coerceIn(0f, originalHeight.toFloat()),
                x2.coerceIn(0f, originalWidth.toFloat()),
                y2.coerceIn(0f, originalHeight.toFloat())
            )

            if (box.width() > 10 && box.height() > 10) {
                detections.add(PriceTagBox(box, confidence))
            }
        }

        return detections
    }

    fun close() {
        synchronized(session ?: Any()) {
            session?.close()
            session = null
            ortEnvironment?.close()
            ortEnvironment = null
        }
    }

    data class PriceTagBox(
        val boundingBox: RectF,
        val confidence: Float
    )

    private data class LetterboxResult(
        val bitmap: Bitmap,
        val scale: Float,
        val padX: Int,
        val padY: Int
    )

    companion object {
        private const val TAG = "PriceTagDetector"
        private const val MODEL_NAME = "best.onnx"
        private const val INPUT_SIZE = 640
        private const val NUM_THREADS = 4
    }
}
