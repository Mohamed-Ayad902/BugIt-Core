package com.example.core.di

import com.example.core.feature.bug_reporting.worker.WorkManagerSyncManager
import com.example.core.feature.bug_reporting.data.repo.BugReporterLocalDS
import com.example.core.feature.bug_reporting.domain.repo.IBackgroundSyncManager
import com.example.core.feature.bug_reporting.domain.repo.IBugReporterLocalDS
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CoreRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBackgroundSyncManager(
        impl: WorkManagerSyncManager
    ): IBackgroundSyncManager

    @Binds
    @Singleton
    abstract fun bindBugReporterLocalDS(
        impl: BugReporterLocalDS
    ): IBugReporterLocalDS
}