package com.example.core.feature.bug_reporting.domain.usecase

import com.example.core.feature.bug_reporting.domain.model.Bug
import com.example.core.feature.bug_reporting.domain.model.BugReportRequest
import com.example.core.feature.bug_reporting.domain.repo.IBackgroundSyncManager
import com.example.core.feature.bug_reporting.domain.repo.IBugReportRepository
import com.example.core_contracts.exceptions.BugItExceptions.ValidationException
import com.example.core_contracts.interactor.LocalUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID
import javax.inject.Inject

class EnqueueBugReportUC @Inject constructor(
    private val repository: IBugReportRepository,
    private val syncManager: IBackgroundSyncManager
) : LocalUseCase<Bug, BugReportRequest>() {

    override fun executeLocal(body: BugReportRequest?): Flow<Bug> = flow {
        if (body == null || body.isInitialState()) throw ValidationException(BugReportRequest::class)

        val bugId = UUID.randomUUID().toString()
        val secureLocalPath = repository.copyImageToLocalDir(body.imageUriString, bugId)
        val bug = repository.saveBug(bugId, body.description, secureLocalPath)
        syncManager.enqueueBugUpload(bugId)

        emit(bug)
    }
}