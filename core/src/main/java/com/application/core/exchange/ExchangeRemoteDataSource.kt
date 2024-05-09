package com.application.core.exchange

import com.application.core.NetworkHandler
import com.application.core.Result
import com.application.core.api.ExchangeApi
import com.application.core.api.processResponse
import com.application.core.api.safeApiCall
import com.application.core.data.model.ExchangeRatesResponse
import javax.inject.Inject

class ExchangeRemoteDataSource @Inject constructor(
    private val networkHandler: NetworkHandler,
    private val exchangeApi: ExchangeApi
) {

    suspend fun getExchangeRate(): Result<ExchangeRatesResponse> {
        return when (networkHandler.isConnected) {
            true -> {
                safeApiCall(
                    call = { callExchangeRateApi(getExchangeParams()) },
                )
            }

            false -> {
                Result.Error(Exception("Network Error"))
            }
        }
    }


    private suspend fun callExchangeRateApi(
        params: HashMap<String, Any>
    ): Result<ExchangeRatesResponse> =
        exchangeApi.getExchangeRate(params).processResponse()


    private fun getExchangeParams(): HashMap<String, Any> {
        val baseParams = HashMap<String, Any>()
        //todo encrypt the key
        baseParams["app_id"] = "995db41eb37d4c0194bdb3e25f8afb91"
        return baseParams
    }


}