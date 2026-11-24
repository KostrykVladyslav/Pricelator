package com.kostryk.pricelator.domain.repository

import com.kostryk.pricelator.domain.AppResult
import com.kostryk.pricelator.domain.model.CurrencyEntity
import kotlinx.coroutines.flow.Flow

interface ExchangeRateRepository {

    suspend fun fetchAndSaveExchangeRates(baseCurrency: String = "USD"): AppResult<Unit>

    fun getAllCurrencies(): Flow<List<CurrencyEntity>>

    fun getAllActiveCurrencies(): Flow<List<CurrencyEntity>>

    suspend fun getCurrencyBySymbol(symbol: String): CurrencyEntity?

    suspend fun updateCurrencyActiveStatus(symbol: String, isActive: Boolean)
}
