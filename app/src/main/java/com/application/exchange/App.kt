package com.application.exchange

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.DelegatingWorkerFactory
import com.application.core.data.model.CoroutinesDispatcherProvider
import com.application.core.exchange.ExchangeLocalDataSource
import com.application.core.worker.CustomWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App:Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var exchangeLocalDataSource: ExchangeLocalDataSource

    @Inject
    lateinit var  dispatcherProvider: CoroutinesDispatcherProvider

    override fun getWorkManagerConfiguration() = Configuration
        .Builder()
        .setWorkerFactory(DelegatingWorkerFactory().apply {
            addFactory(workerFactory)
            addFactory(
                CustomWorkerFactory(
                    exchangeLocalDataSource,
                    dispatcherProvider
                )
            )
        }).build()

}