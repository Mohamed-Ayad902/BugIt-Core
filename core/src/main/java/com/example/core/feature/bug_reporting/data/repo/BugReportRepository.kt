package com.example.core.feature.bug_reporting.data.repo

import com.example.core.feature.bug_reporting.domain.model.Bug
import com.example.core.feature.bug_reporting.domain.model.BugReportRequest
import com.example.core.feature.bug_reporting.domain.repo.IBugReportRepository
import com.example.core.strategies.image.IImageHostStrategy
import com.example.core.utils.IImageCompressor
import com.example.core_contracts.exceptions.BugItExceptions
import com.example.core.strategies.issue_tracker.IIssueTrackerStrategy

internal class BugReportRepository(
    private val imageCompressor: IImageCompressor,
    private val imageHostStrategy: IImageHostStrategy,
    private val issueTrackerStrategy: IIssueTrackerStrategy
) : IBugReportRepository {

    override suspend fun submitBugReport(request: BugReportRequest) : Bug {
        val compressedBytes = imageCompressor.compressImage(request.imageUriString)
            ?: throw BugItExceptions.LocalIOOperation("Failed to compress image")

        val imageUrl = imageHostStrategy.uploadImage(compressedBytes)

        return issueTrackerStrategy.saveIssue(request, imageUrl)
    }
}