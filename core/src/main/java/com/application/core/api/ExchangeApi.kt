package com.application.core.api

import com.application.core.data.model.ExchangeRatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

const val URL_EXCHANGE = "latest.json"
const val BASE_URL = "https://openexchangerates.org/api/"

interface ExchangeApi {

    @GET(URL_EXCHANGE)
    suspend fun getExchangeRate(
        @QueryMap params: HashMap<String, Any>
    ): Response<ExchangeRatesResponse>

}