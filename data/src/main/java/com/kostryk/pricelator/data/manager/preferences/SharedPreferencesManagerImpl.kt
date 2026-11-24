package com.kostryk.pricelator.data.manager.preferences

import android.content.Context
import com.kostryk.pricelator.domain.manager.preferences.SharedPreferencesManager
import androidx.core.content.edit

class SharedPreferencesManagerImpl(private val context: Context) : SharedPreferencesManager {
    private val sharedPreferences =
        context.getSharedPreferences("pricelator_prefs", Context.MODE_PRIVATE)

    override fun <T> put(key: String, value: T) = sharedPreferences.edit {
        when (value) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Float -> putFloat(key, value)
            is Long -> putLong(key, value)
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }

    override fun <T> get(key: String, defValue: T): T = with(sharedPreferences) {
        @Suppress("UNCHECKED_CAST")
        when (defValue) {
            is String -> getString(key, defValue) as T
            is Int -> getInt(key, defValue) as T
            is Boolean -> getBoolean(key, defValue) as T
            is Float -> getFloat(key, defValue) as T
            is Long -> getLong(key, defValue) as T
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }
}