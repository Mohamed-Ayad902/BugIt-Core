package com.example.core.feature.bug_reporting.domain.repo

import com.example.core.feature.bug_reporting.data.entity.BugSyncEntity
import com.example.core.feature.bug_reporting.data.entity.SyncStatusEntity
import com.example.core.feature.bug_reporting.domain.model.Bug
import kotlinx.coroutines.flow.Flow

internal interface IBugReporterLocalDS {
    suspend fun saveBug(bugId: String, description: String, secureLocalPath: String) : BugSyncEntity
    suspend fun updateStatus(id: String, status: SyncStatusEntity, error: String? = null)
    suspend fun updateUploadedImageUrl(id: String, url: String)
    suspend fun copyImageToLocalDir(rawImageUri: String, bugId: String): String
    suspend fun deleteBug(id: String)
    fun observeBugById(bugId: String): Flow<BugSyncEntity?>
    suspend fun getBugsByStatus(status: SyncStatusEntity): List<BugSyncEntity>
}