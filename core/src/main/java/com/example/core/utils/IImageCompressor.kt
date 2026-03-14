package com.example.core.utils

fun interface IImageCompressor {
    /** Takes a URI string and returns the compressed JPEG bytes. */
    suspend fun compressImage(uriString: String): ByteArray?
}