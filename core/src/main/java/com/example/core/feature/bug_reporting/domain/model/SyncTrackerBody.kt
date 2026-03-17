package com.example.core.feature.bug_reporting.domain.model

import com.example.core_contracts.validation.IRequestValidation
import com.example.core_contracts.validation.request.RemoteRequest

data class SyncTrackerBody(
    val request: BugReportRequest,
    val imageUrl: String
): IRequestValidation {
    override fun isInitialState() = request.isInitialState() && imageUrl.isBlank()
    override val remoteMap = RemoteRequest()
}