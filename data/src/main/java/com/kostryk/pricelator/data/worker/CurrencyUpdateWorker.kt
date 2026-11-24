package com.kostryk.pricelator.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kostryk.pricelator.domain.AppResult
import com.kostryk.pricelator.domain.manager.currency.CurrencyUpdateManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CurrencyUpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val currencyUpdateManager: CurrencyUpdateManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return when (val result = currencyUpdateManager.updateCurrenciesIfNeeded()) {
            is AppResult.Success -> Result.success()
            is AppResult.Error -> Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "currency_update_worker"
    }
}
