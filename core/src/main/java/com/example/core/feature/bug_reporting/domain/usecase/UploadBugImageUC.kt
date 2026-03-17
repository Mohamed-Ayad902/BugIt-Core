package com.example.core.feature.bug_reporting.domain.usecase

import com.example.core.feature.bug_reporting.domain.repo.IBugReportRepository
import com.example.core_contracts.exceptions.BugItExceptions
import com.example.core_contracts.interactor.RemoteUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

typealias LocalImage = String
typealias RemoteImage = String

class UploadBugImageUC @Inject constructor(
    private val repo: IBugReportRepository
) : RemoteUseCase<RemoteImage, LocalImage>() {

    override fun executeRemoteDS(body: String?): Flow<String> = flow {
        if (body.isNullOrBlank()) throw BugItExceptions.ValidationException(
            LocalImage::class,
            "Local image path cannot be null"
        )
        emit(repo.uploadImage(body))
    }
}