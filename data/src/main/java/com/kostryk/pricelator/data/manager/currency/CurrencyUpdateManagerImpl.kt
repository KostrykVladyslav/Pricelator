package com.kostryk.pricelator.data.manager.currency

import com.kostryk.pricelator.domain.AppResult
import com.kostryk.pricelator.domain.manager.currency.CurrencyUpdateManager
import com.kostryk.pricelator.domain.manager.preferences.SharedPreferencesManager
import com.kostryk.pricelator.domain.repository.ExchangeRateRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyUpdateManagerImpl @Inject constructor(
    private val exchangeRateRepository: ExchangeRateRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) : CurrencyUpdateManager {

    private val updateMutex = Mutex()

    override suspend fun updateCurrenciesIfNeeded(): AppResult<Unit> {
        return updateMutex.withLock {
            if (shouldUpdateCurrencies()) {
                performUpdate()
            } else {
                AppResult.Success(Unit)
            }
        }
    }

    override suspend fun forceUpdateCurrencies(): AppResult<Unit> {
        return updateMutex.withLock {
            performUpdate()
        }
    }

    private suspend fun performUpdate(): AppResult<Unit> {
        val updateStartTime = System.currentTimeMillis()
        saveLastUpdateTime(updateStartTime)

        val result = exchangeRateRepository.fetchAndSaveExchangeRates()

        if (result is AppResult.Error) {
            val previousTime = updateStartTime - CurrencyUpdateManager.UPDATE_INTERVAL_MILLIS
            saveLastUpdateTime(previousTime)
        }

        return result
    }

    override fun shouldUpdateCurrencies(): Boolean {
        val lastUpdateTime = getLastUpdateTime()
        val currentTime = System.currentTimeMillis()
        val timeSinceLastUpdate = currentTime - lastUpdateTime

        if (timeSinceLastUpdate < CurrencyUpdateManager.COOLDOWN_PERIOD_MILLIS && lastUpdateTime > 0) {
            return false
        }

        return timeSinceLastUpdate >= CurrencyUpdateManager.UPDATE_INTERVAL_MILLIS
    }

    override fun getLastUpdateTime(): Long {
        return sharedPreferencesManager.get(
            SharedPreferencesManager.KEY_LAST_CURRENCY_UPDATE,
            0L
        )
    }

    override fun saveLastUpdateTime(timestamp: Long) {
        sharedPreferencesManager.put(
            SharedPreferencesManager.KEY_LAST_CURRENCY_UPDATE,
            timestamp
        )
    }
}
