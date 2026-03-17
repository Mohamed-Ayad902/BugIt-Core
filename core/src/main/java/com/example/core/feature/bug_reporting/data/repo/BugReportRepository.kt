package com.example.core.feature.bug_reporting.data.repo

import com.example.core.feature.bug_reporting.data.mapper.toDomain
import com.example.core.feature.bug_reporting.data.mapper.toEntity
import com.example.core.feature.bug_reporting.domain.model.Bug
import com.example.core.feature.bug_reporting.domain.model.BugReportRequest
import com.example.core.feature.bug_reporting.domain.model.SyncStatus
import com.example.core.feature.bug_reporting.domain.repo.IBugReportRepository
import com.example.core.feature.bug_reporting.domain.repo.IBugReporterLocalDS
import com.example.core.strategies.image.IImageHostStrategy
import com.example.core.strategies.issue_tracker.IIssueTrackerStrategy
import com.example.core.utils.IImageCompressor
import com.example.core_contracts.exceptions.BugItExceptions
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class BugReportRepository @Inject constructor(
    private val imageCompressor: IImageCompressor,
    private val imageHostStrategy: IImageHostStrategy,
    private val issueTrackerStrategy: IIssueTrackerStrategy,
    private val localDS: IBugReporterLocalDS
) : IBugReportRepository {

    override fun getActiveDestination() = issueTrackerStrategy.destination

    override suspend fun uploadImage(localImagePath: String): String {
        val compressedBytes = imageCompressor.compressImage(localImagePath)
            ?: throw BugItExceptions.LocalIOOperation("Failed to compress image")

        return imageHostStrategy.uploadImage(compressedBytes)
    }

    override suspend fun syncToTracker(request: BugReportRequest, uploadedImageUrl: String) =
        issueTrackerStrategy.saveIssue(request, uploadedImageUrl)

    override suspend fun copyImageToLocalDir(
        imageUriString: String,
        bugId: String
    ) = localDS.copyImageToLocalDir(imageUriString, bugId)

    override suspend fun saveBug(
        bugId: String,
        description: String,
        secureLocalPath: String
    ) = localDS.saveBug(bugId, description, secureLocalPath).toDomain()

    override fun observeBugById(bugId: String) = localDS.observeBugById(bugId).map { it?.toDomain() }

    override suspend fun updateStatus(
        bugId: String,
        status: SyncStatus
    ) {
        localDS.updateStatus(bugId, status.toEntity())
    }

    override suspend fun updateUploadedImageUrl(bugId: String, imageUrl: String) {
        localDS.updateUploadedImageUrl(bugId, imageUrl)
    }

    override suspend fun getBugsByStatus(status: SyncStatus): List<Bug> {
        return localDS.getBugsByStatus(status.toEntity()).map { it.toDomain() }

    }

}