package com.example.core.di

import android.app.Application
import androidx.room.Room
import com.example.core.data_source.local.repository.BugDatabase
import com.example.core.data_source.local.repository.BugSyncDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object LocalModule {

    @Provides
    @Singleton
    fun provideBugDatabase(app: Application) = Room.databaseBuilder(
        app,
        BugDatabase::class.java,
        "bug_database"
    ).fallbackToDestructiveMigration(false).build()

    @Provides
    @Singleton
    fun provideBugSyncDao(db: BugDatabase): BugSyncDao {
        return db.bugSyncDao()
    }
}