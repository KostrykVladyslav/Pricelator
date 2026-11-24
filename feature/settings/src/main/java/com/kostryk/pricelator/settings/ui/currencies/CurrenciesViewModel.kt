package com.kostryk.pricelator.settings.ui.currencies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kostryk.pricelator.domain.AppResult
import com.kostryk.pricelator.domain.ext.toFormattedDateTime
import com.kostryk.pricelator.domain.manager.currency.CurrencyUpdateManager
import com.kostryk.pricelator.domain.manager.preferences.SharedPreferencesManager
import com.kostryk.pricelator.domain.model.CurrencyEntity
import com.kostryk.pricelator.domain.repository.ExchangeRateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class CurrenciesViewModel @Inject constructor(
    private val sharedPreferencesManager: SharedPreferencesManager,
    private val currencyUpdateManager: CurrencyUpdateManager,
    private val exchangeRateRepository: ExchangeRateRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CurrenciesState())
    val state: StateFlow<CurrenciesState> = _state.asStateFlow()

    init {
        loadDefaultCurrencies()
        loadLastUpdateTime()
        loadCurrencies()
    }

    private fun loadCurrencies() {
        viewModelScope.launch {
            exchangeRateRepository.getAllActiveCurrencies().collectLatest { currencies ->
                _state.value = _state.value.copy(currencies = currencies)
            }
        }
    }

    private fun loadDefaultCurrencies() {
        val defaultFrom = sharedPreferencesManager.get(
            SharedPreferencesManager.KEY_DEFAULT_CURRENCY_FROM,
            "USD"
        )
        val defaultTo = sharedPreferencesManager.get(
            SharedPreferencesManager.KEY_DEFAULT_CURRENCY_TO,
            "EUR"
        )

        _state.value = _state.value.copy(
            defaultFromCurrency = defaultFrom,
            defaultToCurrency = defaultTo
        )
    }

    private fun loadLastUpdateTime() {
        val lastUpdate = currencyUpdateManager.getLastUpdateTime()
        _state.value = _state.value.copy(
            lastUpdateTime = if (lastUpdate > 0) lastUpdate.toFormattedDateTime() else "Never"
        )
    }

    fun onDefaultFromCurrencySelected(currencyCode: String) {
        viewModelScope.launch {
            sharedPreferencesManager.put(
                SharedPreferencesManager.KEY_DEFAULT_CURRENCY_FROM,
                currencyCode
            )
            _state.value = _state.value.copy(defaultFromCurrency = currencyCode)
        }
    }

    fun onDefaultToCurrencySelected(currencyCode: String) {
        viewModelScope.launch {
            sharedPreferencesManager.put(
                SharedPreferencesManager.KEY_DEFAULT_CURRENCY_TO,
                currencyCode
            )
            _state.value = _state.value.copy(defaultToCurrency = currencyCode)
        }
    }

    fun onUpdateCurrenciesClicked() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isUpdating = true)

            when (currencyUpdateManager.forceUpdateCurrencies()) {
                is AppResult.Success -> {
                    loadLastUpdateTime()
                    _state.value = _state.value.copy(isUpdating = false)
                }
                is AppResult.Error -> {
                    _state.value = _state.value.copy(
                        isUpdating = false,
                        errorMessage = "Failed to update currencies"
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }
}

data class CurrenciesState(
    val defaultFromCurrency: String = "UAH",
    val defaultToCurrency: String = "USD",
    val lastUpdateTime: String = "Never",
    val isUpdating: Boolean = false,
    val errorMessage: String? = null,
    val currencies: List<CurrencyEntity> = emptyList()
)
