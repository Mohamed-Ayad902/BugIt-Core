package com.example.core.feature.bug_reporting.domain.repo

import com.example.core.feature.bug_reporting.domain.model.Bug
import com.example.core.feature.bug_reporting.domain.model.BugReportRequest

interface IBugReportRepository {
    suspend fun submitBugReport(request: BugReportRequest): Bug
}