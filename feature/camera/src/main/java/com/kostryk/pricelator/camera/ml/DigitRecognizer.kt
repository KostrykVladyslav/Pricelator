package com.kostryk.pricelator.camera.ml

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.nio.FloatBuffer

class DigitRecognizer(private val context: Context) {

    private var ortEnvironment: OrtEnvironment? = null
    private var session: OrtSession? = null
    private var vocabulary: List<String> = emptyList()

    init {
        loadVocabulary()
        setupOnnx()
    }

    private fun loadVocabulary() {
        try {
            vocabulary = context.assets.open(VOCAB_FILE).bufferedReader().readLines()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load vocabulary: ${e.message}", e)
        }
    }

    private fun setupOnnx() {
        ortEnvironment = OrtEnvironment.getEnvironment()
        val modelBytes = try {
            context.assets.open(MODEL_NAME).readBytes()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load model: ${e.message}", e)
            return
        }

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

    fun recognize(bitmap: Bitmap): String? {
        val currentSession = session ?: return null
        if (vocabulary.isEmpty()) return null

        return try {
            synchronized(currentSession) {
                val t0 = System.nanoTime()
                val inputTensor = preprocessImage(bitmap)
                val t1 = System.nanoTime()
                val inputName = currentSession.inputNames.first()

                val results = currentSession.run(mapOf(inputName to inputTensor))
                val t2 = System.nanoTime()

                val outputTensor = results.first().value as OnnxTensor
                val outputShape = outputTensor.info.shape

                val floatBuffer = outputTensor.floatBuffer
                val output = FloatArray(floatBuffer.remaining())
                floatBuffer.get(output)

                inputTensor.close()
                outputTensor.close()
                results.close()

                val decoded = ctcGreedyDecode(output, outputShape)
                val t3 = System.nanoTime()

                Log.d(TAG, "⏱ preprocess=${(t1-t0)/1_000_000}ms inference=${(t2-t1)/1_000_000}ms decode=${(t3-t2)/1_000_000}ms")

                postprocessPrice(decoded)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Recognition failed: ${e.message}", e)
            null
        }
    }



    private fun preprocessImage(bitmap: Bitmap): OnnxTensor {
        val targetHeight = IMG_HEIGHT
        val effectiveHeight = maxOf(bitmap.height, MIN_UPSCALE_HEIGHT)
        val scale = targetHeight.toFloat() / effectiveHeight
        val scaledWidth = (bitmap.width * (effectiveHeight.toFloat() / bitmap.height) * scale)
            .toInt().coerceIn(1, IMG_WIDTH)
        val resized = Bitmap.createScaledBitmap(bitmap, scaledWidth, targetHeight, true)

        val targetWidth = IMG_WIDTH
        val pixels = IntArray(scaledWidth * targetHeight)
        resized.getPixels(pixels, 0, scaledWidth, 0, 0, scaledWidth, targetHeight)

        var minLum = 255
        var maxLum = 0
        for (pixel in pixels) {
            val lum = ((pixel shr 16 and 0xFF) * 77 +
                    (pixel shr 8 and 0xFF) * 150 +
                    (pixel and 0xFF) * 29) shr 8
            if (lum < minLum) minLum = lum
            if (lum > maxLum) maxLum = lum
        }
        val contrastRange = maxLum - minLum
        val needsEnhancement = contrastRange in 30..200
        val contrastScale = if (needsEnhancement) 255f / contrastRange else 1f

        val floatBuffer = FloatBuffer.allocate(1 * 3 * targetHeight * targetWidth)
        val padValue = 1.0f

        for (c in 0 until 3) {
            for (y in 0 until targetHeight) {
                for (x in 0 until targetWidth) {
                    if (x < scaledWidth) {
                        val pixel = pixels[y * scaledWidth + x]
                        val raw = when (c) {
                            0 -> pixel shr 16 and 0xFF
                            1 -> pixel shr 8 and 0xFF
                            2 -> pixel and 0xFF
                            else -> 0
                        }
                        val channelValue = if (needsEnhancement) {
                            ((raw - minLum) * contrastScale).toInt().coerceIn(0, 255)
                        } else raw
                        floatBuffer.put((channelValue / 255.0f - 0.5f) / 0.5f)
                    } else {
                        floatBuffer.put(padValue)
                    }
                }
            }
        }
        floatBuffer.rewind()

        return OnnxTensor.createTensor(
            ortEnvironment,
            floatBuffer,
            longArrayOf(1, 3, targetHeight.toLong(), targetWidth.toLong())
        )
    }

    private fun ctcGreedyDecode(output: FloatArray, shape: LongArray): String {
        val seqLength = shape[1].toInt()
        val numClasses = shape[2].toInt()

        val result = StringBuilder()
        var prevIdx = 0

        for (t in 0 until seqLength) {
            var maxIdx = 0
            var maxVal = Float.NEGATIVE_INFINITY

            val offset = t * numClasses
            for (c in 0 until numClasses) {
                val value = output[offset + c]
                if (value > maxVal) {
                    maxVal = value
                    maxIdx = c
                }
            }

            if (maxIdx != 0 && maxIdx != prevIdx) {
                val charIdx = maxIdx - 1
                if (charIdx in vocabulary.indices) {
                    val char = vocabulary[charIdx]
                    if (char.length == 1 && (char[0].isDigit() || char[0] == '.' || char[0] == ',')) {
                        result.append(char)
                    }
                }
            }
            prevIdx = maxIdx
        }

        return result.toString()
    }

    private fun postprocessPrice(raw: String): String? {
        if (raw.isBlank()) return null

        var result = raw

        if (result.matches(Regex("^0+$"))) return null

        if (result.matches(Regex("^0[1-9]\\d*[.,]\\d+$"))) {
            result = result.substring(1)
        }

        if (result.matches(Regex("^00[1-9]\\d*[.,]\\d+$"))) {
            result = result.substring(1)
        }

        if (result.matches(Regex("^0[1-9]\\d*$")) && result.length > 1) {
            result = result.substring(1)
        }

        if (result.matches(Regex("^\\d+[.,]\\d{2}0+$"))) {
            result = result.replace(Regex("0+$"), "")
        }

        if (!result.any { it.isDigit() && it != '0' }) return null

        return result
    }

    fun close() {
        synchronized(session ?: Any()) {
            session?.close()
            session = null
            ortEnvironment?.close()
            ortEnvironment = null
        }
    }

    companion object {
        private const val TAG = "DigitRecognizer"
        private const val MODEL_NAME = "PP-OCRv5_mobile_rec.onnx"
        private const val VOCAB_FILE = "ppocr_keys_v1.txt"
        private const val IMG_HEIGHT = 48
        private const val IMG_WIDTH = 320
        private const val MIN_UPSCALE_HEIGHT = 150
        private const val NUM_THREADS = 4
    }
}
