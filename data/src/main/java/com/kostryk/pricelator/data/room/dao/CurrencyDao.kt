package com.kostryk.pricelator.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kostryk.pricelator.domain.model.CurrencyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    @Query("SELECT * FROM currency WHERE isActive = 1")
    fun getAllActiveCurrencies(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currency")
    fun getAllCurrencies(): Flow<List<CurrencyEntity>>

    @Query("SELECT * FROM currency WHERE symbol = :symbol LIMIT 1")
    suspend fun getCurrencyBySymbol(symbol: String): CurrencyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrencies(currencies: List<CurrencyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrency(currency: CurrencyEntity)

    @Update
    suspend fun updateCurrency(currency: CurrencyEntity)

    @Query("DELETE FROM currency")
    suspend fun deleteAllCurrencies()

    @Query("UPDATE currency SET isActive = :isActive WHERE symbol = :symbol")
    suspend fun updateCurrencyActiveStatus(symbol: String, isActive: Boolean)
}
