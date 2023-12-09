package com.application.core.exchange

import com.application.core.data.local.ExchangeEntity
import com.application.core.data.local.dao.ExchangeDao
import com.application.core.data.local.mapper.ExchangeEntityToDomainMapper
import com.application.core.data.model.ExchangeRatesResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ExchangeLocalDataSource @Inject constructor(
    private val exchangeDao: ExchangeDao,
    private val exchangeEntityToDomainMapper: ExchangeEntityToDomainMapper,
) {
    fun getExchangeRates(): Flow<List<ExchangeEntity>> {
        return exchangeDao.getExchangeRate()
    }

    suspend fun deleteAllExchangeRates() {
        exchangeDao.deleteAllExchangeRates()
    }

    suspend fun insertExchangeRates(exchangeRatesResponse: ExchangeRatesResponse) =
        exchangeDao.insert(exchangeEntityToDomainMapper.mapTo(exchangeRatesResponse))


}