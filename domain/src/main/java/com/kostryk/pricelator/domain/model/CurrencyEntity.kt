package com.kostryk.pricelator.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class CurrencyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val symbol: String,
    val exchangeRate: Double,
    val isActive: Boolean = true
)
