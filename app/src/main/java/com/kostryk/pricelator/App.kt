package com.kostryk.pricelator

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kostryk.pricelator.data.worker.CurrencyUpdateWorker
import com.kostryk.pricelator.domain.manager.currency.CurrencyUpdateManager
import com.kostryk.pricelator.domain.manager.language.LanguageManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var currencyUpdateManager: CurrencyUpdateManager

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var languageManager: LanguageManager

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        initializeLanguage()
        schedulePeriodicCurrencyUpdates()
        checkAndUpdateCurrencies()
    }

    private fun initializeLanguage() {
        val currentLanguage = languageManager.getLanguage()
        val localeList = LocaleListCompat.forLanguageTags(currentLanguage.code)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    private fun schedulePeriodicCurrencyUpdates() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<CurrencyUpdateWorker>(
            repeatInterval = 7,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                CurrencyUpdateWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )
    }

    private fun checkAndUpdateCurrencies() {
        applicationScope.launch {
            currencyUpdateManager.updateCurrenciesIfNeeded()
        }
    }
}