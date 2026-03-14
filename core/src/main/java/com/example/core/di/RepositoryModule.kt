package com.example.core.di

import android.content.Context
import com.example.core.feature.bug_reporting.data.repo.BugReportRepository
import com.example.core.feature.bug_reporting.domain.repo.IBugReportRepository
import com.example.core.strategies.image.IImageHostStrategy
import com.example.core.strategies.image.ImgBBHostStrategy
import com.example.core.strategies.issue_tracker.GoogleSheetsTrackerStrategy
import com.example.core.strategies.issue_tracker.IIssueTrackerStrategy
import com.example.core.utils.AndroidImageCompressor
import com.example.core.utils.IImageCompressor
import com.example.core.utils.NativeKeys
import com.example.core_contracts.data_source.remote.INetworkProvider
import com.google.api.services.sheets.v4.Sheets
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

    @Provides
    @Singleton
    fun provideImageCompressor(@ApplicationContext context: Context): IImageCompressor {
        return AndroidImageCompressor(context)
    }

    @Provides
    @Singleton
    fun provideImageHostStrategy(
        networkProvider: INetworkProvider
    ): IImageHostStrategy {
        return ImgBBHostStrategy(networkProvider)
    }

    @Provides
    @Singleton
    fun provideIssueTrackerStrategy(
        sheetsService: Sheets,
        @ApplicationContext context: Context
    ): IIssueTrackerStrategy {
        return GoogleSheetsTrackerStrategy(sheetsService, NativeKeys.getSpreadsheetId(context))
    }

    @Provides
    @Singleton
    fun provideBugReportRepository(
        imageCompressor: IImageCompressor,
        imageHostStrategy: IImageHostStrategy,
        issueTrackerStrategy: IIssueTrackerStrategy
    ): IBugReportRepository {
        return BugReportRepository(
            imageCompressor = imageCompressor,
            imageHostStrategy = imageHostStrategy,
            issueTrackerStrategy = issueTrackerStrategy
        )
    }
}