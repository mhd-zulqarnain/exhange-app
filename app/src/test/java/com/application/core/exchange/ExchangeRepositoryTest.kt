package com.application.core.exchange

import com.application.core.Result
import com.application.core.data.local.ExchangeEntity
import com.application.core.data.local.mapper.ExchangeEntityToDomainMapper
import com.application.core.worker.WorkerRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.lang.reflect.Field
class ExchangeRepositoryTest {

    private val remoteDataSource: ExchangeRemoteDataSource = mock()
    private val localDataSource: ExchangeLocalDataSource = mock()
    private val exchangeEntityToDomainMapper: ExchangeEntityToDomainMapper = mock()
    private val workerRepository: WorkerRepository = mock()
    private val mapperSpy = Mockito.spy(exchangeEntityToDomainMapper) // Spy on mapper for verification

    private val repository = ExchangeRepository(
        remoteDataSource,
        localDataSource,
        mapperSpy,
        workerRepository
    )

    @Test
    fun `success with remote API when database is empty`() = runBlocking {
        // Prepare mock data for successful remote API response:
        withSuccessRemoteData()
        val expectedList = mapperSpy.mapTo(apiResponse) // Expected mapped domain model list

        // Mock empty database:
        whenever(localDataSource.getExchangeRates()).thenReturn(flowOf(listOf()))

        // Execute repository's getExchangeRate() and verify results:
        repository.getExchangeRate().test {
            val actualResult = expectItem() // Get emitted item
            Assert.assertEquals(actualResult, Result.Success(expectedList)) // Verify success and data
            expectComplete() // Verify flow completion
        }
    }

    @Test
    fun `success with local data when database is not empty`() = runBlocking {
        // Mock pre-populated database:
        whenever(localDataSource.getExchangeRates()).thenReturn(flowOf(entityList))

        // Execute repository's getExchangeRate() and verify results:
        repository.getExchangeRate().test {
            val actualResult = expectItem() // Get emitted item
            Assert.assertEquals(actualResult, Result.Success(entityList)) // Verify success and data
            expectComplete() // Verify flow completion
        }
    }

    @Test
    fun `success with cached data`() = runBlocking {
        // Prepare mock cache with data:
        val cache = arrayListOf(ExchangeEntity(1, "ALL", 94.537512))
        setField(repository, repository.javaClass.getDeclaredField("cacheResult"), cache) // Set cache field using reflection

        // Execute repository's getExchangeRate() and verify results:
        repository.getExchangeRate().test {
            val actualResult = expectItem() // Get emitted item
            Assert.assertEquals(actualResult, Result.Success(cache)) // Verify success and data
            expectComplete() // Verify flow completion
        }
    }

    @Test
    fun `failure when API fails and database is empty`() = runBlocking {
        // Mock API error and empty database:
        val error = Result.Error(Exception(""))
        whenever(localDataSource.getExchangeRates()).thenReturn(flowOf(listOf()))
        whenever(remoteDataSource.getExchangeRate()).doReturn(error)

        // Execute repository's getExchangeRate() and verify results:
        repository.getExchangeRate().test {
            val actualResult = expectItem() // Get emitted item
            Assert.assertEquals(actualResult, error) // Verify error result
            expectComplete() // Verify flow completion
        }
    }

    // Helper function to mock successful remote data response:
    private suspend fun withSuccessRemoteData() {
        whenever(remoteDataSource.getExchangeRate()).doReturn(Result.Success(apiResponse))
    }

}


fun setField(`object`: Any?, fld: Field?, value: Any?) {
    try {
        fld?.isAccessible = true
        fld?.set(`object`, value)
    } catch (e: IllegalAccessException) {
        val fieldName = if (null == fld) "n/a" else fld.name
        throw RuntimeException("Failed to set $fieldName of object", e)
    }
}
