package com.example.core.feature.bug_reporting.domain.usecase

import com.example.core.feature.bug_reporting.domain.repo.IBugReportRepository
import com.example.core.model.ReportingDestination
import javax.inject.Inject

class GetActiveTrackerUC @Inject constructor(
    private val repo: IBugReportRepository
) {
    operator fun invoke(): ReportingDestination = repo.getActiveDestination()
}