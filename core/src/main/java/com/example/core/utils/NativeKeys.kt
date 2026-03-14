package com.example.core.utils

import android.content.Context

internal object NativeKeys {
    init {
        System.loadLibrary("bugitkeys")
    }

    external fun getImgBBApiKey(context: Context): String
    external fun getSpreadsheetId(context: Context): String
    external fun getGoogleCredentialsJson(context: Context): String
}