package com.example.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

internal class AndroidImageCompressor @Inject constructor(
    private val context: Context
) : IImageCompressor {

    override suspend fun compressImage(uriString: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val uri = uriString.toUri()
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    val outputStream = ByteArrayOutputStream()

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
                    outputStream.toByteArray()
                }
            } catch (_: Exception) {
                null
            }
        }
    }
}