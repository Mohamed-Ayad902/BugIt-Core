package com.example.core.feature.bug_reporting.domain.usecase

import com.example.core.feature.bug_reporting.domain.model.Bug
import com.example.core.feature.bug_reporting.domain.model.SyncTrackerBody
import com.example.core.feature.bug_reporting.domain.repo.IBugReportRepository
import com.example.core_contracts.exceptions.BugItExceptions
import com.example.core_contracts.interactor.RemoteUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SyncBugToTrackerUC @Inject constructor(
    private val repo: IBugReportRepository
) : RemoteUseCase<Bug, SyncTrackerBody>() {

    override fun executeRemoteDS(body: SyncTrackerBody?): Flow<Bug> = flow {
        if (body == null || body.request.isInitialState())
            throw BugItExceptions.ValidationException(
                SyncTrackerBody::class,
                "Please provide a valid request and image url"
            )
        emit(repo.syncToTracker(body.request, body.imageUrl))
    }
}