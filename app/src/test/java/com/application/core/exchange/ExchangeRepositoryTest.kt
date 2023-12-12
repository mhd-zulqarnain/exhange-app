package com.application.core.exchange

import com.application.core.data.local.mapper.ExchangeEntityToDomainMapper
import com.application.core.worker.WorkerRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import com.application.core.Result
import com.application.core.data.model.ExchangeRatesResponse
import org.mockito.Mockito

class ExchangeRepositoryTest {
    private val remoteDataSource: ExchangeRemoteDataSource = mock()
    private val localDataSource: ExchangeLocalDataSource = mock()
    private val exchangeEntityToDomainMapper: ExchangeEntityToDomainMapper = mock()
    private val workerRepository: WorkerRepository = mock()
    private val mapperSpy = Mockito.spy(exchangeEntityToDomainMapper)

    private val repository = ExchangeRepository(
        remoteDataSource,
        localDataSource,
        mapperSpy,
        workerRepository
    )

    private  val currencyRates = mapOf(
        "USD" to 237.20, // PKR per USD
        "EUR" to 269.64, // PKR per EUR
        "AMD" to 1.00 // PKR per PKR (obviously 1)
    )
    private   val apiResponse = ExchangeRatesResponse("","",12123,"",currencyRates)

    @Test
    fun `success with remote api`() = runBlocking {
        withSuccessRemoteData()
        val expectedList = mapperSpy.mapTo(apiResponse)
        repository.getExchangeRate().test {
            val expectItem = expectItem()
//            verify(localDataSource).insertExchangeRates(apiResponse)
            Assert.assertEquals(expectItem, Result.Success(expectedList))
            expectComplete()
        }
    }
    private suspend fun withSuccessRemoteData() {

        whenever(remoteDataSource.getExchangeRate()).doReturn(Result.Success(apiResponse))
    }

}