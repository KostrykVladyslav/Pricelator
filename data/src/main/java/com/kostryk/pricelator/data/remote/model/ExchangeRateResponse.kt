package com.kostryk.pricelator.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateResponse(
    @SerialName("result")
    val result: String,
    @SerialName("documentation")
    val documentation: String? = null,
    @SerialName("terms_of_use")
    val termsOfUse: String? = null,
    @SerialName("time_last_update_unix")
    val timeLastUpdateUnix: Long,
    @SerialName("time_last_update_utc")
    val timeLastUpdateUtc: String,
    @SerialName("time_next_update_unix")
    val timeNextUpdateUnix: Long,
    @SerialName("time_next_update_utc")
    val timeNextUpdateUtc: String,
    @SerialName("base_code")
    val baseCode: String,
    @SerialName("conversion_rates")
    val conversionRates: Map<String, Double>
)
