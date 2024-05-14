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

    @ColumnInfo(name = "convertedAmount")
    var convertedAmount: Double = 0.0
) {
    fun getConvertedAmount(amount: Double, desiredCurrencyRate: Double): Double {
        val convertedRate = rate / desiredCurrencyRate
        val convertedAmount = amount * convertedRate
        return convertedAmount
    }
}

class ExchangeModel(
    var symbol: String,
    var amount : Double,
    var rate: Double
)