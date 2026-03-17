package com.example.core.feature.bug_reporting.worker

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.core.feature.bug_reporting.domain.repo.IBackgroundSyncManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class WorkManagerSyncManager @Inject constructor(
    private val workManager: WorkManager
) : IBackgroundSyncManager {

    override fun enqueueBugUpload(bugId: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<BugUploadWorker>()
            .setConstraints(constraints)
            .setInputData(Data.Builder().putString(BugUploadWorker.KEY_BUG_ID, bugId).build())
            .addTag(bugId)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueue(workRequest)
    }

    override fun cancelBugUpload(bugId: String) {
        workManager.cancelAllWorkByTag(bugId)
    }

    override fun schedulePeriodicRetrySweep() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicRequest = PeriodicWorkRequestBuilder<RetryFailedBugsWorker>(12, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "PeriodicFailedBugSweep",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }
}