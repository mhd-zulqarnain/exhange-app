package com.application.core.exchange

import com.application.core.data.local.ExchangeEntity
import com.application.core.data.local.mapper.DataMapper
import com.application.core.data.model.ExchangeRatesResponse
import javax.inject.Inject


class FakeExchangeEntityToDomainMapper constructor() :
    DataMapper<List<ExchangeEntity>, ExchangeRatesResponse> {
    override fun mapTo(value: ExchangeRatesResponse): List<ExchangeEntity> {
        val list = arrayListOf<ExchangeEntity>()
        value.currencyRates.forEach {
            list.add(ExchangeEntity(symbol = it.key, rate = it.value))
        }
        return list
    }

}