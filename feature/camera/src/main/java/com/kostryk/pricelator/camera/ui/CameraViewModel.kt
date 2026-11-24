package com.kostryk.pricelator.camera.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.graphics.RectF
import com.kostryk.pricelator.camera.ml.DebugInfo
import com.kostryk.pricelator.camera.ml.DetectedTextBlock
import com.kostryk.pricelator.domain.ext.toFormattedDateTime
import com.kostryk.pricelator.domain.manager.currency.CurrencyUpdateManager
import com.kostryk.pricelator.domain.manager.preferences.SharedPreferencesManager
import com.kostryk.pricelator.domain.manager.preferences.SharedPreferencesManager.Companion.KEY_DEFAULT_CURRENCY_FROM
import com.kostryk.pricelator.domain.manager.preferences.SharedPreferencesManager.Companion.KEY_DEFAULT_CURRENCY_TO
import com.kostryk.pricelator.domain.model.CurrencyEntity
import com.kostryk.pricelator.domain.usecase.GetExchangeRatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val getExchangeRatesUseCase: GetExchangeRatesUseCase,
    private val currencyUpdateManager: CurrencyUpdateManager,
    private val preferencesManager: SharedPreferencesManager
) : ViewModel() {

    private val _state = MutableStateFlow(CameraState())
    val state: StateFlow<CameraState> = _state.asStateFlow()

    init {
        loadCurrencies()
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            getExchangeRatesUseCase.observeAllCurrencies().collect { currencies ->
                val lastUpdate = currencyUpdateManager.getLastUpdateTime()
                val defaultSource = preferencesManager.get(KEY_DEFAULT_CURRENCY_FROM, "USD")
                val defaultTarget = preferencesManager.get(KEY_DEFAULT_CURRENCY_TO, "EUR")

                _state.value = _state.value.copy(
                    currencies = currencies,
                    sourceCurrency = currencies.firstOrNull { it.symbol == defaultSource } ?: currencies.firstOrNull(),
                    targetCurrency = currencies.firstOrNull { it.symbol == defaultTarget } ?: currencies.getOrNull(1),
                    lastUpdateTime = if (lastUpdate > 0) lastUpdate.toFormattedDateTime() else ""
                )
            }
        }
    }

    fun refreshSettings() {
        val currentCurrencies = _state.value.currencies
        if (currentCurrencies.isNotEmpty()) {
            val defaultSource = preferencesManager.get(KEY_DEFAULT_CURRENCY_FROM, "USD")
            val defaultTarget = preferencesManager.get(KEY_DEFAULT_CURRENCY_TO, "EUR")

            _state.value = _state.value.copy(
                sourceCurrency = currentCurrencies.firstOrNull { it.symbol == defaultSource } ?: _state.value.sourceCurrency,
                targetCurrency = currentCurrencies.firstOrNull { it.symbol == defaultTarget } ?: _state.value.targetCurrency
            )
        }
    }

    fun onTextDetected(text: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                detectedText = text,
                error = null
            )

            parseAndConvert(text)
        }
    }

    fun onDebugInfo(debugInfo: DebugInfo) {
        _state.value = _state.value.copy(
            debugBlocks = debugInfo.blocks,
            imageWidth = debugInfo.imageWidth,
            imageHeight = debugInfo.imageHeight,
            imageRotation = debugInfo.rotation - 90,
        )
    }

    private fun parseAndConvert(text: String) {
        val amount = extractAmount(text)

        if (amount != null && _state.value.sourceCurrency != null && _state.value.targetCurrency != null) {
            val converted = convertCurrency(
                amount,
                _state.value.sourceCurrency!!,
                _state.value.targetCurrency!!
            )

            _state.value = _state.value.copy(
                detectedAmount = amount,
                convertedAmount = converted,
                isScanning = false
            )
        }
    }

    private fun extractAmount(text: String): Double? {
        val numberPattern = Regex("""[-+]?\d*\.?\d+(?:[eE][-+]?\d+)?""")
        val matches = numberPattern.findAll(text)

        val result = matches.mapNotNull { match ->
            val cleaned = match.value.replace(",", ".")
            cleaned.toDoubleOrNull()
        }.maxOrNull()
        return result
    }

    private fun convertCurrency(
        amount: Double,
        from: CurrencyEntity,
        to: CurrencyEntity
    ): Double {
        val amountInUsd = amount / from.exchangeRate
        return amountInUsd * to.exchangeRate
    }

    fun selectSourceCurrency(currencyCode: String) {
        val currency = _state.value.currencies.find { it.symbol == currencyCode }
        if (currency != null) {
            preferencesManager.put(KEY_DEFAULT_CURRENCY_FROM, currency.symbol)
            if (currency.symbol == _state.value.targetCurrency?.symbol) {
                _state.value = _state.value.copy(
                    sourceCurrency = _state.value.targetCurrency,
                    targetCurrency = currency
                )
            } else {
                _state.value = _state.value.copy(sourceCurrency = currency)
            }

            _state.value.detectedAmount?.let { amount ->
                val converted = convertCurrency(
                    amount,
                    _state.value.sourceCurrency!!,
                    _state.value.targetCurrency!!
                )
                _state.value = _state.value.copy(convertedAmount = converted)
            }
        }
    }

    fun selectTargetCurrency(currencyCode: String) {
        val currency = _state.value.currencies.find { it.symbol == currencyCode }
        if (currency != null) {
            preferencesManager.put(KEY_DEFAULT_CURRENCY_TO, currency.symbol)
            if (currency.symbol == _state.value.sourceCurrency?.symbol) {
                _state.value = _state.value.copy(
                    sourceCurrency = _state.value.targetCurrency,
                    targetCurrency = currency
                )
            } else {
                _state.value = _state.value.copy(targetCurrency = currency)
            }

            _state.value.detectedAmount?.let { amount ->
                val converted = convertCurrency(
                    amount,
                    _state.value.sourceCurrency!!,
                    _state.value.targetCurrency!!
                )
                _state.value = _state.value.copy(convertedAmount = converted)
            }
        }
    }

    fun toggleFlashlight() {
        _state.value = _state.value.copy(
            isFlashlightOn = !_state.value.isFlashlightOn
        )
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun onError(error: CameraError) {
        _state.value = _state.value.copy(
            error = error,
            isScanning = true
        )
    }

    fun resetScanning() {
        _state.value = _state.value.copy(
            isScanning = true,
            detectedAmount = null,
            convertedAmount = null
        )
    }

    fun onPriceTagResults(results: List<PriceTagResult>, imageWidth: Int, imageHeight: Int) {
        android.util.Log.d("CameraViewModel", "onPriceTagResults: ${results.size} tags, image: ${imageWidth}x${imageHeight}")
        _state.value = _state.value.copy(
            priceTagResults = results,
            priceTagImageWidth = imageWidth,
            priceTagImageHeight = imageHeight
        )
    }
}

data class CameraState(
    val detectedText: String = "",
    val detectedAmount: Double? = null,
    val convertedAmount: Double? = null,
    val sourceCurrency: CurrencyEntity? = null,
    val targetCurrency: CurrencyEntity? = null,
    val currencies: List<CurrencyEntity> = emptyList(),
    val lastUpdateTime: String = "",
    val isScanning: Boolean = true,
    val isFlashlightOn: Boolean = false,
    val error: CameraError? = null,
    val debugBlocks: List<DetectedTextBlock> = emptyList(),
    val imageWidth: Int = 0,
    val imageHeight: Int = 0,
    val imageRotation: Int = 0,
    // Price tag detection results
    val priceTagResults: List<PriceTagResult> = emptyList(),
    val priceTagImageWidth: Int = 0,
    val priceTagImageHeight: Int = 0,
)

sealed class CameraError {
    data class RecognitionFailed(val message: String) : CameraError()
    data class CameraAccessFailed(val message: String) : CameraError()
    data object PermissionDenied : CameraError()
}

data class PriceTagResult(
    val boundingBox: RectF,
    val confidence: Float,
    val recognizedText: String?
)
