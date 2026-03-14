package com.example.core.strategies.issue_tracker

import com.example.core.feature.bug_reporting.domain.model.Bug
import com.example.core.feature.bug_reporting.domain.model.BugReportRequest

interface IIssueTrackerStrategy {
    suspend fun saveIssue(request: BugReportRequest, uploadedImageUrl: String): Bug
}