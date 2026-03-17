package com.example.core.feature.bug_reporting.data.mapper

import com.example.core.feature.bug_reporting.data.entity.BugSyncEntity
import com.example.core.feature.bug_reporting.data.entity.SyncStatusEntity
import com.example.core.feature.bug_reporting.domain.model.Bug
import com.example.core.feature.bug_reporting.domain.model.SyncStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal fun BugSyncEntity.toDomain(): Bug {
    return Bug(
        id = this.id,
        description = this.description,
        screenshotUri = this.remoteImageUrl ?: this.localImagePath,
        dynamicFields = emptyMap(),
        createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(this.createdAt)),
        status = this.status.toDomain(),
        remoteImageUrl = this.remoteImageUrl ?: ""
    )
}

internal fun SyncStatusEntity.toDomain(): SyncStatus {
    return when (this) {
        SyncStatusEntity.PENDING -> SyncStatus.PENDING
        SyncStatusEntity.UPLOADING -> SyncStatus.UPLOADING
        SyncStatusEntity.COMPLETED -> SyncStatus.COMPLETED
        SyncStatusEntity.FAILED -> SyncStatus.FAILED
    }
}

internal fun SyncStatus.toEntity(): SyncStatusEntity {
    return when (this) {
        SyncStatus.PENDING -> SyncStatusEntity.PENDING
        SyncStatus.UPLOADING -> SyncStatusEntity.UPLOADING
        SyncStatus.COMPLETED -> SyncStatusEntity.COMPLETED
        SyncStatus.FAILED -> SyncStatusEntity.FAILED
    }
}