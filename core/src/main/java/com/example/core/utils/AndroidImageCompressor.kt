package com.example.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.core.net.toUri
import com.example.core_contracts.extensions.loge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import javax.inject.Inject

internal class AndroidImageCompressor @Inject constructor(
    private val context: Context
) : IImageCompressor {

    private val TAG = "BugItCompressor"

    override suspend fun compressImage(uriString: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val isContentUri = uriString.startsWith("content://") || uriString.startsWith("android.resource://")

                if (!isContentUri) {
                    val file = File(uriString)
                    if (!file.exists()) {
                        return@withContext null
                    }
                    if (file.length() == 0L) {
                        return@withContext null
                    }
                }

                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }

                getInputStream(uriString)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream, null, options)
                }

                options.inSampleSize = calculateInSampleSize(options, 1080, 1920)
                options.inJustDecodeBounds = false

                val bitmap = getInputStream(uriString)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream, null, options)
                }

                if (bitmap == null) return@withContext null

                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)

                bitmap.recycle()

                val resultBytes = outputStream.toByteArray()

                resultBytes
            } catch (e: Exception) {
                "Error while compressing: $e".loge(TAG)
                null
            }
        }
    }

    private fun getInputStream(pathOrUri: String): InputStream? {
        return if (pathOrUri.startsWith("content://") || pathOrUri.startsWith("android.resource://")) {
            context.contentResolver.openInputStream(pathOrUri.toUri())
        } else {
            File(pathOrUri).inputStream()
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}