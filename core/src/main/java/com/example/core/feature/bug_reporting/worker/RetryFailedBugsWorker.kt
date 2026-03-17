package com.example.core.feature.bug_reporting.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.core.feature.bug_reporting.domain.model.SyncStatus
import com.example.core.feature.bug_reporting.domain.repo.IBugReportRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
internal class RetryFailedBugsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val localDS: IBugReportRepository,
    private val syncManager: WorkManagerSyncManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val failedBugs = localDS.getBugsByStatus(SyncStatus.FAILED)

            failedBugs.forEach { bug ->
                syncManager.enqueueBugUpload(bug.id)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}