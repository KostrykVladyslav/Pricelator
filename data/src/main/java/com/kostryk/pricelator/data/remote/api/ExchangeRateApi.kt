package com.kostryk.pricelator.data.remote.api

import com.kostryk.pricelator.data.remote.model.ExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApi {

    @GET("v6/{apiKey}/latest/{baseCurrency}")
    suspend fun getLatestRates(
        @Path("apiKey") apiKey: String,
        @Path("baseCurrency") baseCurrency: String = "USD"
    ): ExchangeRateResponse
}
