package com.kostryk.pricelator.data.repository

import com.kostryk.pricelator.data.remote.api.ExchangeRateApi
import com.kostryk.pricelator.data.remote.mapper.ExchangeRateMapper
import com.kostryk.pricelator.data.room.dao.CurrencyDao
import com.kostryk.pricelator.data.util.DefaultCurrencyProvider
import com.kostryk.pricelator.domain.AppDispatcher
import com.kostryk.pricelator.domain.AppResult
import com.kostryk.pricelator.domain.ExceptionHandler
import com.kostryk.pricelator.domain.model.CurrencyEntity
import com.kostryk.pricelator.domain.repository.ExchangeRateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExchangeRateRepositoryImpl @Inject constructor(
    private val api: ExchangeRateApi,
    private val currencyDao: CurrencyDao,
    private val exceptionHandler: ExceptionHandler,
    private val appDispatcher: AppDispatcher,
    private val defaultCurrencyProvider: DefaultCurrencyProvider,
    private val apiKey: String
) : ExchangeRateRepository {

    override suspend fun fetchAndSaveExchangeRates(baseCurrency: String): AppResult<Unit> {
        return withContext(appDispatcher.IO) {
            // Try to fetch from API
            val apiResult = exceptionHandler.handle(appDispatcher.IO) {
                val response = api.getLatestRates(
                    apiKey = apiKey,
                    baseCurrency = baseCurrency
                )

                val currencyEntities = ExchangeRateMapper.mapToCurrencyEntities(response)
                currencyDao.deleteAllCurrencies()
                currencyDao.insertCurrencies(currencyEntities)
            }

            if (apiResult is AppResult.Error) {
                val existingCurrencies = currencyDao.getAllCurrencies().first()

                if (existingCurrencies.isEmpty()) {
                    val defaultResponse = defaultCurrencyProvider.getDefaultExchangeRates()

                    if (defaultResponse != null) {
                        val currencyEntities =
                            ExchangeRateMapper.mapToCurrencyEntities(defaultResponse)
                        currencyDao.insertCurrencies(currencyEntities)
                        return@withContext AppResult.Success(Unit)
                    }
                }
            }

            apiResult
        }
    }

    override fun getAllCurrencies(): Flow<List<CurrencyEntity>> {
        return currencyDao.getAllCurrencies()
    }

    override fun getAllActiveCurrencies(): Flow<List<CurrencyEntity>> {
        return currencyDao.getAllActiveCurrencies()
    }

    override suspend fun getCurrencyBySymbol(symbol: String): CurrencyEntity? {
        return currencyDao.getCurrencyBySymbol(symbol)
    }

    override suspend fun updateCurrencyActiveStatus(symbol: String, isActive: Boolean) {
        currencyDao.updateCurrencyActiveStatus(symbol, isActive)
    }
}
