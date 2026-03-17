package com.example.core.data_source.local.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.core.feature.bug_reporting.data.entity.BugSyncEntity

@Database(entities = [BugSyncEntity::class], version = 1, exportSchema = false)
internal abstract class BugDatabase : RoomDatabase() {
    abstract fun bugSyncDao(): BugSyncDao
}