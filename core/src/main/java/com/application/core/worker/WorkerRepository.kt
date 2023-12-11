package com.application.core.worker

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkerRepository @Inject constructor(
    @ApplicationContext
    val context: Context,
) {
    fun scheduleUpdateExchangeRate() {
        val delay = (System.currentTimeMillis() + (30.minutes())) - System.currentTimeMillis()
        val notificationWork =
            OneTimeWorkRequest.Builder(ExchangeWorker::class.java)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS).build()
        val instanceWorkManager = WorkManager.getInstance(context)

        instanceWorkManager.beginUniqueWork(
            "UpdateExchangeRateWorker",
            ExistingWorkPolicy.REPLACE,
            notificationWork
        ).enqueue()
    }
}

fun Int.minutes(): Int {
    return this * 60 * 1000
}