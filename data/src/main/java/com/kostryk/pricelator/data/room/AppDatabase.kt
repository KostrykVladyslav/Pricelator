package com.kostryk.pricelator.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kostryk.pricelator.data.room.dao.CurrencyDao
import com.kostryk.pricelator.domain.model.CurrencyEntity

@Database(
    entities = [CurrencyEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao
}