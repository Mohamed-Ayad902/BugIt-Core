package com.example.core.feature.bug_reporting.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bug_sync_queue")
internal data class BugSyncEntity(
    @PrimaryKey val id: String,
    val description: String,
    val localImagePath: String,
    val remoteImageUrl: String? = null,
    val status: SyncStatusEntity,
    val failureMessage: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

internal enum class SyncStatusEntity {
    PENDING, UPLOADING, COMPLETED, FAILED
}