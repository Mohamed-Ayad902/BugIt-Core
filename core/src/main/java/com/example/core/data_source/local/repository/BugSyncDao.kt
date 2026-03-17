package com.example.core.data_source.local.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.core.feature.bug_reporting.data.entity.BugSyncEntity
import com.example.core.feature.bug_reporting.data.entity.SyncStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface BugSyncDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(bug: BugSyncEntity)

    @Query("UPDATE bug_sync_queue SET status = :status, failureMessage = :error WHERE id = :id")
    suspend fun updateStatus(id: String, status: SyncStatusEntity, error: String? = null)

    @Query("UPDATE bug_sync_queue SET remoteImageUrl = :url WHERE id = :id")
    suspend fun updateRemoteImageUrl(id: String, url: String)

    @Query("SELECT * FROM bug_sync_queue WHERE status = :status")
    suspend fun getBugsByStatus(status: SyncStatusEntity): List<BugSyncEntity>

    @Query("DELETE FROM bug_sync_queue WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM bug_sync_queue WHERE id = :id")
    fun observeById(id: String): Flow<BugSyncEntity?>
}