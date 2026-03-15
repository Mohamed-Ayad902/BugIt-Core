package com.example.core.feature.bug_reporting.domain.repo

import com.example.core.feature.bug_reporting.domain.model.Bug
import com.example.core.feature.bug_reporting.domain.model.BugReportRequest
import com.example.core.model.ReportingDestination

interface IBugReportRepository {
    fun getActiveDestination(): ReportingDestination
    suspend fun submitBugReport(request: BugReportRequest): Bug
}