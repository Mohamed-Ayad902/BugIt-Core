package com.example.core.feature.bug_reporting.domain.repo

interface IBackgroundSyncManager {
    fun enqueueBugUpload(bugId: String)
    fun cancelBugUpload(bugId: String)
    fun schedulePeriodicRetrySweep()
}