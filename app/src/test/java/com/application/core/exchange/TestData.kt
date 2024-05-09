package com.application.core.exchange

import com.application.core.data.local.ExchangeEntity
import com.application.core.data.model.ExchangeRatesResponse

val currencyRates = mapOf(
    "USD" to 237.20, // PKR per USD
    "EUR" to 269.64, // PKR per EUR
    "AMD" to 1.00 // PKR per PKR (obviously 1)
)

val entityList = listOf(ExchangeEntity(1, "ALL", 94.537512), ExchangeEntity(1, "AED", 3.6724))

val apiResponse = ExchangeRatesResponse("", "", 12123, "", currencyRates)
