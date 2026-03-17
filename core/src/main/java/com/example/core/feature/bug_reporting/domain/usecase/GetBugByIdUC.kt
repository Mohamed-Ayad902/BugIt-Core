package com.example.core.feature.bug_reporting.domain.usecase

import com.example.core.feature.bug_reporting.domain.model.Bug
import com.example.core.feature.bug_reporting.domain.repo.IBugReportRepository
import com.example.core_contracts.exceptions.BugItExceptions.ValidationException
import com.example.core_contracts.interactor.LocalUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBugByIdUC @Inject constructor(
    private val repository: IBugReportRepository,
) : LocalUseCase<Bug?, String>() {

    override fun executeLocal(body: String?): Flow<Bug?> {
        if (body.isNullOrEmpty()) throw ValidationException(
            String::class,
            "Your forgot to pass the bug id"
        )
        return repository.observeBugById(body)
    }
}