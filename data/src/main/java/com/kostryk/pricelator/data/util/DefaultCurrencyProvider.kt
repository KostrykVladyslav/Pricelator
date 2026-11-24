package com.kostryk.pricelator.data.util

import android.content.Context
import com.kostryk.pricelator.data.remote.model.ExchangeRateResponse
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DefaultCurrencyProvider @Inject constructor(
    private val context: Context,
    private val json: Json
) {

    fun getDefaultExchangeRates(): ExchangeRateResponse? {
        return try {
            val jsonString = context.assets.open("default_exchange_rates.json")
                .bufferedReader()
                .use { it.readText() }

            json.decodeFromString<ExchangeRateResponse>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
