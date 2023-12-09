package com.application.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.application.core.data.local.ExchangeEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ExchangeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<ExchangeEntity>)

    @Query("DELETE FROM exchange_rates")
    suspend fun deleteAllExchangeRates()

    @Query("SELECT * FROM exchange_rates")
    fun getExchangeRate() : Flow<List<ExchangeEntity>>

    @Query("SELECT * FROM exchange_rates WHERE symbol = :symbol")
    fun getExchangeRateBySymbol(symbol:String): ExchangeEntity
}