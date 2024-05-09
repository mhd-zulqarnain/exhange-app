package com.application.core.data.model

import com.google.gson.annotations.SerializedName

data class ExchangeRatesResponse(
    val disclaimer: String,
    val license: String,
    val timestamp: Long,
    val base: String,
    @SerializedName("rates")
    val currencyRates: Map<String, Double>
)