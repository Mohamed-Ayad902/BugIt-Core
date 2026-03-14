package com.example.core.di

import android.content.Context
import com.example.core.R
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

        val credentials = context.resources.openRawResource(R.raw.google_credentials).use { inputStream ->
            GoogleCredentials.fromStream(inputStream)
                .createScoped(listOf(SheetsScopes.SPREADSHEETS))
        }

        return Sheets.Builder(transport, jsonFactory, HttpCredentialsAdapter(credentials))
            .setApplicationName("BugIt App")
            .build()
    }
}