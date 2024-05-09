package com.application.core.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.application.core.data.model.CoroutinesDispatcherProvider
import com.application.core.exchange.ExchangeLocalDataSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withContext

const val ACTION_CLEAR_LOCAL_CACHE = "clear local cache"

@HiltWorker
class ExchangeWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted params: WorkerParameters,
    private val exchangeLocalDataSource: ExchangeLocalDataSource,
    private val dispatcherProvider: CoroutinesDispatcherProvider,

    ) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result = withContext(dispatcherProvider.io) {
        exchangeLocalDataSource.deleteAllExchangeRates()
        Log.i("ExchangeWorker", "ExchangeWorker:doWork")
        LocalBroadcastManager.getInstance(context).sendBroadcast(
            Intent(
                ACTION_CLEAR_LOCAL_CACHE
            )
        )
        return@withContext Result.success()
    }
}