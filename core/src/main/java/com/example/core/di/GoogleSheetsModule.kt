package com.example.core.di

import android.content.Context
import com.example.core.utils.NativeKeys
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
import java.io.ByteArrayInputStream
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object GoogleSheetsModule {

    @Provides
    @Singleton
    fun provideGoogleSheetsService(@ApplicationContext context: Context): Sheets {
        val transport = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory = GsonFactory.getDefaultInstance()

        val jsonString = NativeKeys.getGoogleCredentialsJson(context)
        val inputStream = ByteArrayInputStream(jsonString.toByteArray(Charsets.UTF_8))

        val credentials = GoogleCredentials.fromStream(inputStream)
            .createScoped(listOf(SheetsScopes.SPREADSHEETS))

        return Sheets.Builder(transport, jsonFactory, HttpCredentialsAdapter(credentials))
            .setApplicationName("BugIt App")
            .build()
    }
}