package com.application.core.data.local
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rates")
data class ExchangeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "rate")
    val rate: Double,

)