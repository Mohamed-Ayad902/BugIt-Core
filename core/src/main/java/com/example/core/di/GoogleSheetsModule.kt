package com.example.core.di

import android.content.Context
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object GoogleSheetsModule {

    @Provides
    @Singleton
    fun provideGoogleSheetsService(@ApplicationContext context: Context): Sheets {
        val transport = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()

        // 2. Updated Credential Loading Logic
        // In your finalized code, you will use:
        // val inputStream: InputStream = context.resources.openRawResource(R.raw.google_credentials)
        // val credentials = GoogleCredentials.fromStream(inputStream)
        //     .createScoped(listOf(SheetsScopes.SPREADSHEETS))

        // FOR NOW: Creating a placeholder GoogleCredentials object that won't trigger deprecation
        // Note: HttpCredentialsAdapter is the bridge between the new auth library and the old Sheets library
        val credentials = GoogleCredentials.create(null) // Placeholder
            .createScoped(listOf(SheetsScopes.SPREADSHEETS))

        return Sheets.Builder(transport, jsonFactory, HttpCredentialsAdapter(credentials))
            .setApplicationName("BugIt App")
            .build()
    }
}