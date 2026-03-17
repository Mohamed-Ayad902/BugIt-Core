package com.example.core.strategies.issue_tracker

import com.example.core.feature.bug_reporting.domain.model.Bug
import com.example.core.feature.bug_reporting.domain.model.BugReportRequest
import com.example.core.feature.bug_reporting.domain.model.SyncStatus
import com.example.core.model.ReportingDestination
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.AddSheetRequest
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest
import com.google.api.services.sheets.v4.model.Request
import com.google.api.services.sheets.v4.model.SheetProperties
import com.google.api.services.sheets.v4.model.ValueRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

internal class GoogleSheetsTrackerStrategy(
    private val sheetsService: Sheets,
    private val spreadsheetId: String
) : IIssueTrackerStrategy {

    override val destination: ReportingDestination = ReportingDestination.GOOGLE_SHEETS

    // stores the name of the tab we have already verified in this session
    private var verifiedTabCache: String? = null

    override suspend fun saveIssue(request: BugReportRequest, uploadedImageUrl: String): Bug {
        return withContext(Dispatchers.IO) {
            val tabName = getCurrentDateTabName()

            val headerRow: List<Any> = listOf(
                "Bug ID",
                "Timestamp",
                "Description",
                "Screenshot URL"
            ) + request.dynamicFields.keys

            ensureTabExists(tabName, headerRow)

            val generatedId = UUID.randomUUID().toString()
            val fullTimestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val rowValues: List<Any> = listOf(
                generatedId,
                fullTimestamp,
                request.description,
                uploadedImageUrl
            ) + request.dynamicFields.values

            val body = ValueRange().setValues(listOf(rowValues))

            sheetsService.spreadsheets().values()
                .append(spreadsheetId, "$tabName!A1", body)
                .setValueInputOption("USER_ENTERED")
                .execute()

            Bug(
                id = generatedId,
                description = request.description,
                screenshotUri = uploadedImageUrl,
                dynamicFields = request.dynamicFields,
                createdAt = fullTimestamp,
                status = SyncStatus.COMPLETED,
                remoteImageUrl = uploadedImageUrl
            )
        }
    }

    private fun getCurrentDateTabName(): String {
        val formatter = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
        return formatter.format(Date())
    }

    /**
     * Checks if a sheet with [tabName] exists.
     * If not, creates it and instantly appends the [headers] to Row 1.
     */
    private fun ensureTabExists(tabName: String, headers: List<Any>) {
        if (verifiedTabCache == tabName) return

        val spreadsheet = sheetsService.spreadsheets()[spreadsheetId].execute()
        val existingTabs = spreadsheet.sheets.map { it.properties.title }

        if (!existingTabs.contains(tabName)) {
            val addSheetRequest = AddSheetRequest().apply {
                properties = SheetProperties().setTitle(tabName)
            }

            val batchUpdateRequest = BatchUpdateSpreadsheetRequest().apply {
                requests = listOf(Request().setAddSheet(addSheetRequest))
            }

            sheetsService.spreadsheets()
                .batchUpdate(spreadsheetId, batchUpdateRequest)
                .execute()

            val headerBody = ValueRange().setValues(listOf(headers))
            sheetsService.spreadsheets().values()
                .append(spreadsheetId, "$tabName!A1", headerBody)
                .setValueInputOption("USER_ENTERED")
                .execute()
        }

        verifiedTabCache = tabName
    }
}