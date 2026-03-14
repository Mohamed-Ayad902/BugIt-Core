package com.example.core.strategies.image

import com.example.core_contracts.data_source.remote.INetworkProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

internal class ImgBBHostStrategy(
    private val networkProvider: INetworkProvider
) : IImageHostStrategy {

    override suspend fun uploadImage(imageBytes: ByteArray, expirationSeconds: Int?): String {
        val tempFile = withContext(Dispatchers.IO) {
            File.createTempFile("bug_screenshot_${UUID.randomUUID()}", ".png")
        }
        tempFile.writeBytes(imageBytes)

        val filesMap = hashMapOf("image" to tempFile)

        return try {
            val response = networkProvider.postWithImagesFile<ImgBBDto, Any>(
                responseWrappedModel = ImgBBDto::class.java,
                pathUrl = "upload",
                queryParams = expirationSeconds?.let { hashMapOf("expiration" to it) },
                requestBody = null,
                files = filesMap
            )
            response.data.url
        } finally {
            if (tempFile.exists()) tempFile.delete()
        }
    }
}