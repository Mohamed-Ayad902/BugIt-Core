package com.example.core.strategies.issue_tracker

import com.example.core.feature.bug_reporting.domain.model.BugReportRequest
import com.example.core_contracts.extensions.logw
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

/**
 * Implementation of [IIssueTrackerStrategy] that logs bug reports into Google Sheets.
 * Each day's reports are stored in a separate tab named by the current date (dd-MM-yy).
 *
 * @property sheetsService The authorized Google Sheets API service instance.
 * @property spreadsheetId The unique identifier of the target spreadsheet.
 */
internal class GoogleSheetsTrackerStrategy(
    private val sheetsService: Sheets,
    private val spreadsheetId: String
) : IIssueTrackerStrategy {

    /**
     * Appends a bug report as a new row in the spreadsheet.
     * Maps [request] fields and the [uploadedImageUrl] into a flat row structure.
     */
    override suspend fun saveIssue(request: BugReportRequest, uploadedImageUrl: String) {
        "Saving bug report to Google Sheets... $request -- $uploadedImageUrl".logw("GoogleSheetsTrackerStrategy")
        withContext(Dispatchers.IO) {
            val tabName = getCurrentDateTabName()

            ensureTabExists(tabName)

            val rowValues: List<Any> = listOf(
                request.description,
                uploadedImageUrl
            ) + request.dynamicFields.values

            val body = ValueRange().setValues(listOf(rowValues))

            sheetsService.spreadsheets().values()
                .append(spreadsheetId, "$tabName!A1", body)
                .setValueInputOption("USER_ENTERED")
                .execute()
        }
    }

    private fun getCurrentDateTabName(): String {
        val formatter = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
        return formatter.format(Date())
    }

    /**
     * Checks if a sheet with [tabName] exists; if not, creates it via a batch update.
     */
    private fun ensureTabExists(tabName: String) {
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
        }
    }
}