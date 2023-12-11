package com.application.core.exchange

import com.application.core.Result
import com.application.core.data.local.ExchangeEntity
import com.application.core.data.local.mapper.ExchangeEntityToDomainMapper
import com.application.core.worker.WorkerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ExchangeRepository @Inject constructor(
    private val remoteDataSource: ExchangeRemoteDataSource,
    private val localDataSource: ExchangeLocalDataSource,
    private val exchangeEntityToDomainMapper: ExchangeEntityToDomainMapper,
    private val workerRepository: WorkerRepository
) {
    private var cacheResult: ArrayList<ExchangeEntity>? = null

    fun getExchangeRate(): Flow<Result<List<ExchangeEntity>>> = flow {
        try {
            // Check if cached result exists
            cacheResult?.let { cachedData ->
                emit(Result.Success(cachedData))
                return@flow // Exit the flow if cached data exists
            }

            // Check if local database has data
            localDataSource.getExchangeRates().first().let { dbData ->
                if (dbData.isNotEmpty()) {
                    emit(Result.Success(dbData))
                    cacheResult(dbData)
                    return@flow // Exit the flow if database has data
                }
            }

            // Fetch data from remote source if no cached or database data exists
            when (val result = remoteDataSource.getExchangeRate()) {
                is Result.Success -> {

                    val data = result.data
                    val list = exchangeEntityToDomainMapper.mapTo(data) // Map data to domain model
                    localDataSource.insertExchangeRates(data) // Save data to local database
                    workerRepository.scheduleUpdateExchangeRate()  //schedule to get updated data
                    cacheResult(list)
                    emit(Result.Success(list))
                }

                is Result.Error -> {
                    emit(result) // Emit error if data fetching fails
                }

                else -> {
                    // Handle unexpected results
                    emit(Result.Error(Exception("Unexpected result fetching data")))
                }
            }

        } catch (e: Exception) {
            emit(Result.Error(e)) // Emit error if any exception occurs
        }
    }


    fun cacheResult(list: List<ExchangeEntity>) {
        cacheResult?.clear()
        cacheResult?.addAll(list)
    }

    fun clearCache() {
        cacheResult?.clear()
    }
}