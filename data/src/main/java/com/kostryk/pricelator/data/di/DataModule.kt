package com.kostryk.pricelator.data.di

import android.content.Context
import androidx.room.Room
import com.kostryk.pricelator.data.exception.ExceptionHandlerImpl
import com.kostryk.pricelator.data.manager.currency.CurrencyUpdateManagerImpl
import com.kostryk.pricelator.data.manager.file.FileManagerImpl
import com.kostryk.pricelator.data.manager.language.LanguageManagerImpl
import com.kostryk.pricelator.data.manager.preferences.SharedPreferencesManagerImpl
import com.kostryk.pricelator.data.manager.theme.ThemeManagerImpl
import com.kostryk.pricelator.data.remote.api.ExchangeRateApi
import com.kostryk.pricelator.data.repository.ExchangeRateRepositoryImpl
import com.kostryk.pricelator.data.room.AppDatabase
import com.kostryk.pricelator.data.room.dao.CurrencyDao
import com.kostryk.pricelator.data.util.DefaultCurrencyProvider
import com.kostryk.pricelator.domain.AppDispatcher
import com.kostryk.pricelator.domain.ExceptionHandler
import com.kostryk.pricelator.domain.manager.currency.CurrencyUpdateManager
import com.kostryk.pricelator.domain.manager.file.FileManager
import com.kostryk.pricelator.domain.manager.language.LanguageManager
import com.kostryk.pricelator.domain.manager.preferences.SharedPreferencesManager
import com.kostryk.pricelator.domain.manager.theme.ThemeManager
import com.kostryk.pricelator.domain.repository.ExchangeRateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    fun provideFileManager(@ApplicationContext context: Context): FileManager {
        return FileManagerImpl(context)
    }

    @Provides
    fun providePreferencesManagerManager(@ApplicationContext context: Context): SharedPreferencesManager {
        return SharedPreferencesManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pricelator_database"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    @Singleton
    fun provideCurrencyDao(database: AppDatabase): CurrencyDao {
        return database.currencyDao()
    }

    @Provides
    @Singleton
    fun provideExceptionHandler(): ExceptionHandler {
        return ExceptionHandlerImpl()
    }

    @Provides
    @Singleton
    fun provideDefaultCurrencyProvider(@ApplicationContext context: Context): DefaultCurrencyProvider {
        val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
        return DefaultCurrencyProvider(context, json)
    }

    @Provides
    @Singleton
    fun provideExchangeRateRepository(
        api: ExchangeRateApi,
        currencyDao: CurrencyDao,
        exceptionHandler: ExceptionHandler,
        appDispatcher: AppDispatcher,
        defaultCurrencyProvider: DefaultCurrencyProvider,
        @Named("exchangeRateApiKey") apiKey: String
    ): ExchangeRateRepository {
        return ExchangeRateRepositoryImpl(
            api = api,
            currencyDao = currencyDao,
            exceptionHandler = exceptionHandler,
            appDispatcher = appDispatcher,
            defaultCurrencyProvider = defaultCurrencyProvider,
            apiKey = apiKey
        )
    }

    @Provides
    @Singleton
    fun provideCurrencyUpdateManager(
        exchangeRateRepository: ExchangeRateRepository,
        sharedPreferencesManager: SharedPreferencesManager
    ): CurrencyUpdateManager {
        return CurrencyUpdateManagerImpl(
            exchangeRateRepository = exchangeRateRepository,
            sharedPreferencesManager = sharedPreferencesManager
        )
    }

    @Provides
    @Singleton
    fun provideThemeManager(
        sharedPreferencesManager: SharedPreferencesManager
    ): ThemeManager {
        return ThemeManagerImpl(sharedPreferencesManager)
    }

    @Provides
    @Singleton
    fun provideLanguageManager(
        sharedPreferencesManager: SharedPreferencesManager
    ): LanguageManager {
        return LanguageManagerImpl(sharedPreferencesManager)
    }
}