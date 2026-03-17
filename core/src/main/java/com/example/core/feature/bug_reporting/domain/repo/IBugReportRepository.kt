package com.example.core.feature.bug_reporting.domain.repo

import com.example.core.feature.bug_reporting.data.entity.SyncStatusEntity
import com.example.core.feature.bug_reporting.domain.model.Bug
import com.example.core.feature.bug_reporting.domain.model.BugReportRequest
import com.example.core.feature.bug_reporting.domain.model.SyncStatus
import com.example.core.model.ReportingDestination
import kotlinx.coroutines.flow.Flow

interface IBugReportRepository {
    fun getActiveDestination(): ReportingDestination
    suspend fun uploadImage(localImagePath: String): String
    suspend fun syncToTracker(request: BugReportRequest, uploadedImageUrl: String): Bug
    suspend fun copyImageToLocalDir(imageUriString: String, bugId: String): String
    suspend fun saveBug(bugId: String, description: String, secureLocalPath: String): Bug
    fun observeBugById(bugId: String): Flow<Bug?>
    suspend fun updateStatus(bugId: String, status: SyncStatus)
    suspend fun updateUploadedImageUrl(bugId: String, imageUrl: String)
    suspend fun getBugsByStatus(status: SyncStatus) : List<Bug>
}