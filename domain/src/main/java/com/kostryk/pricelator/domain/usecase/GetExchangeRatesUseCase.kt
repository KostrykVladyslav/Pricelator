package com.kostryk.pricelator.domain.usecase

import com.kostryk.pricelator.domain.AppResult
import com.kostryk.pricelator.domain.model.CurrencyEntity
import com.kostryk.pricelator.domain.repository.ExchangeRateRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExchangeRatesUseCase @Inject constructor(
    private val repository: ExchangeRateRepository
) {

    suspend fun fetchAndSaveRates(baseCurrency: String = "USD"): AppResult<Unit> {
        return repository.fetchAndSaveExchangeRates(baseCurrency)
    }

    fun observeActiveCurrencies(): Flow<List<CurrencyEntity>> {
        return repository.getAllActiveCurrencies()
    }

    fun observeAllCurrencies(): Flow<List<CurrencyEntity>> {
        return repository.getAllCurrencies()
    }

    suspend fun getCurrencyBySymbol(symbol: String): CurrencyEntity? {
        return repository.getCurrencyBySymbol(symbol)
    }

    suspend fun updateCurrencyActiveStatus(symbol: String, isActive: Boolean) {
        repository.updateCurrencyActiveStatus(symbol, isActive)
    }
}
