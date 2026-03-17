package com.example.core.feature.bug_reporting.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.core.feature.bug_reporting.domain.model.BugReportRequest
import com.example.core.feature.bug_reporting.domain.model.SyncStatus
import com.example.core.feature.bug_reporting.domain.model.SyncTrackerBody
import com.example.core.feature.bug_reporting.domain.repo.IBugReportRepository
import com.example.core.feature.bug_reporting.domain.usecase.SyncBugToTrackerUC
import com.example.core.feature.bug_reporting.domain.usecase.UploadBugImageUC
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import java.io.File

@HiltWorker
internal class BugUploadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: IBugReportRepository,
    private val uploadImageUC: UploadBugImageUC,
    private val syncToTrackerUC: SyncBugToTrackerUC
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val bugId = inputData.getString(KEY_BUG_ID) ?: return Result.failure()
        val entity = repository.observeBugById(bugId).first() ?: return Result.success()
        if (entity.status == SyncStatus.COMPLETED) return Result.success()

        return try {
            repository.updateStatus(bugId, SyncStatus.UPLOADING)

            var imageUrl = entity.remoteImageUrl

            if (imageUrl.isBlank()) {
                imageUrl = uploadImageUC.executeRemoteDS(entity.screenshotUri).first()
                repository.updateUploadedImageUrl(bugId, imageUrl)
            }
            val request = BugReportRequest(
                description = entity.description,
                imageUriString = entity.screenshotUri
            )
            val syncBody = SyncTrackerBody(request, imageUrl)
            syncToTrackerUC.executeRemoteDS(syncBody).first()

            repository.updateStatus(bugId, SyncStatus.COMPLETED)
            File(entity.screenshotUri).delete()

            Result.success()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            e.printStackTrace()

            repository.updateStatus(bugId, SyncStatus.FAILED)
            Result.retry()
        }
    }

    companion object {
        const val KEY_BUG_ID = "bug_id"
    }
}