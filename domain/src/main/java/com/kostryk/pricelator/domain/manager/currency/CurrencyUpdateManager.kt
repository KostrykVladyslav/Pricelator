package com.kostryk.pricelator.domain.manager.currency

import com.kostryk.pricelator.domain.AppResult

interface CurrencyUpdateManager {

    suspend fun updateCurrenciesIfNeeded(): AppResult<Unit>

    suspend fun forceUpdateCurrencies(): AppResult<Unit>

    fun shouldUpdateCurrencies(): Boolean

    fun getLastUpdateTime(): Long

    fun saveLastUpdateTime(timestamp: Long)

    companion object {
        const val UPDATE_INTERVAL_MILLIS = 7L * 24 * 60 * 60 * 1000
        const val COOLDOWN_PERIOD_MILLIS = 5L * 60 * 1000
    }
}
