package com.application.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.application.core.data.local.dao.ExchangeDao

@Database(entities = [ExchangeEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME: String = "exchange_db"
    }

    abstract fun exchangeDao(): ExchangeDao
}