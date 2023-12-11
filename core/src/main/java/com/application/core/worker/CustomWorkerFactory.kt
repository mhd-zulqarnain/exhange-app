package com.application.core.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.application.core.data.model.CoroutinesDispatcherProvider
import com.application.core.exchange.ExchangeLocalDataSource

class CustomWorkerFactory(
    private var exchangeLocalDataSource: ExchangeLocalDataSource,
    private var dispatcherProvider: CoroutinesDispatcherProvider
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            ExchangeWorker::class.java.name ->
                ExchangeWorker(
                    appContext,
                    workerParameters,
                    exchangeLocalDataSource,
                    dispatcherProvider
                )
            else ->
                // Return null, so that the base class can delegate to the default WorkerFactory.
                null
        }
    }

}