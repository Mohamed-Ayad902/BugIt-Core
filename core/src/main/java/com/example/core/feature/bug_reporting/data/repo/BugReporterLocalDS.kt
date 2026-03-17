package com.example.core.feature.bug_reporting.data.repo

import android.content.Context
import androidx.core.net.toUri
import com.example.core.data_source.local.repository.BugSyncDao
import com.example.core.feature.bug_reporting.data.entity.BugSyncEntity
import com.example.core.feature.bug_reporting.data.entity.SyncStatusEntity
import com.example.core.feature.bug_reporting.domain.repo.IBugReporterLocalDS
import com.example.core_contracts.exceptions.BugItExceptions.LocalIOOperation
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

internal class BugReporterLocalDS @Inject constructor(
    private val bugSyncDao: BugSyncDao,
    @ApplicationContext private val context: Context
): IBugReporterLocalDS {
    override suspend fun saveBug(bugId: String, description: String, secureLocalPath: String) : BugSyncEntity {
        val entity = BugSyncEntity(
            id = bugId,
            description = description,
            localImagePath = secureLocalPath,
            status = SyncStatusEntity.PENDING,
            createdAt = System.currentTimeMillis()
        )
        bugSyncDao.insertOrUpdate(entity)
        return entity
    }
    override suspend fun updateStatus(id: String, status: SyncStatusEntity, error: String?) = bugSyncDao.updateStatus(id, status, error)
    override suspend fun updateUploadedImageUrl(id: String, url: String) = bugSyncDao.updateRemoteImageUrl(id, url)
    override suspend fun deleteBug(id: String) = bugSyncDao.deleteById(id)
    override fun observeBugById(bugId: String): Flow<BugSyncEntity?> = bugSyncDao.observeById(bugId)
    override suspend fun getBugsByStatus(status: SyncStatusEntity) = bugSyncDao.getBugsByStatus(status)

    override suspend fun copyImageToLocalDir(rawImageUri: String, bugId: String): String {
        val inputStream =
            context.contentResolver.openInputStream(rawImageUri.toUri())
                ?: throw LocalIOOperation("Failed to open input stream")
        val destFile = File(context.filesDir, "bug_image_$bugId.jpg")
        val outputStream = withContext(Dispatchers.IO) {
            FileOutputStream(destFile)
        }
        inputStream.use { input -> outputStream.use { output -> input.copyTo(output) } }
        return destFile.absolutePath
    }
}