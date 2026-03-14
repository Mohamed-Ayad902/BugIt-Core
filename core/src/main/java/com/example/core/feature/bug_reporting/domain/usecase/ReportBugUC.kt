package com.example.core.feature.bug_reporting.domain.usecase

import com.example.core.feature.bug_reporting.domain.model.Bug
import com.example.core.feature.bug_reporting.domain.model.BugReportRequest
import com.example.core.feature.bug_reporting.domain.repo.IBugReportRepository
import com.example.core_contracts.exceptions.BugItExceptions.ValidationException
import com.example.core_contracts.interactor.RemoteUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReportBugUC @Inject constructor(
    private val repo: IBugReportRepository
) : RemoteUseCase<Bug, BugReportRequest>() {
    override fun executeRemoteDS(body: BugReportRequest?): Flow<Bug> = flow {
        if (body == null || body.isInitialState()) throw ValidationException(BugReportRequest::class)

        emit(repo.submitBugReport(body))
    }

}